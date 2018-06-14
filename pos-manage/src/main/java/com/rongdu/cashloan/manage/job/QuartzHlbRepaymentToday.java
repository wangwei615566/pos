package com.rongdu.cashloan.manage.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.czwx.helipay.service.PayService;
import com.czwx.helipay.service.impl.PayServiceImpl;
import com.czwx.helipay.vo.CreateOrderVo;
import com.czwx.helipay.vo.QueryOrderVo;
import com.rongdu.cashloan.cl.domain.BankCard;
import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.domain.Channel;
import com.rongdu.cashloan.cl.domain.PayLog;
import com.rongdu.cashloan.cl.domain.PayReqLog;
import com.rongdu.cashloan.cl.model.BorrowRepayLogModel;
import com.rongdu.cashloan.cl.model.BorrowRepayModel;
import com.rongdu.cashloan.cl.model.PayLogModel;
import com.rongdu.cashloan.cl.service.BankCardService;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ChannelService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.ClSmsService;
import com.rongdu.cashloan.cl.service.PayLogService;
import com.rongdu.cashloan.cl.service.PayReqLogService;
import com.rongdu.cashloan.cl.service.SyncLoanInfoKnService;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.common.util.OrderNoUtil;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.domain.User;
import com.rongdu.cashloan.core.domain.UserBaseInfo;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.cashloan.core.service.CloanUserService;
import com.rongdu.cashloan.core.service.UserBaseInfoService;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;

import tool.util.BeanUtil;
import tool.util.BigDecimalUtil;
import tool.util.DateUtil;

/**
 * 合利宝自动扣款还款(针对当天到期的)
 * @author wangwei
 *
 */
@Component
@Lazy(value = false)
public class QuartzHlbRepaymentToday implements Job {

	private static final Logger logger = Logger.getLogger(QuartzHlbRepaymentToday.class);

	/**
	 * 扣款还款
	 * 
	 * @throws ServiceException
	 */
	public String repayment() throws ServiceException {

		CloanUserService cloanUserService = (CloanUserService) BeanUtil.getBean("cloanUserService");
		UserBaseInfoService userBaseInfoService = (UserBaseInfoService) BeanUtil.getBean("userBaseInfoService");
		BankCardService bankCardService = (BankCardService) BeanUtil.getBean("bankCardService");
		ClBorrowService clBorrowService = (ClBorrowService) BeanUtil.getBean("clBorrowService");
		BorrowRepayService borrowRepayService = (BorrowRepayService) BeanUtil.getBean("borrowRepayService");
		PayLogService payLogService = (PayLogService) BeanUtil.getBean("payLogService");
		ClSmsService clSmsService = (ClSmsService) BeanUtil.getBean("clSmsService");
		PayReqLogService payReqLogService = (PayReqLogService) BeanUtil.getBean("payReqLogService");
		ChannelService channelService = (ChannelService) BeanUtil.getBean("channelService");
		SyncLoanInfoKnService syncLoanInfoKnService = (SyncLoanInfoKnService) BeanUtil.getBean("syncLoanInfoKnService");
		// 查询待还款记录
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Date repayTime = DateUtil.valueOf(DateUtil.dateStr2(DateUtil.getNow()) + " 23:59:59", DateUtil.DATEFORMAT_STR_001);
		paramMap.put("repayTime", repayTime);
		paramMap.put("state", BorrowRepayModel.STATE_REPAY_NO);
		List<BorrowRepay> borrowRepayList = borrowRepayService.findUnRepayToday(paramMap);

		logger.info("当天进入自动扣款还款...");
		String quartzRemark = null;
		int succeed = 0;
		int fail = 0;
		int total = 0;
		PayLog payLog = null;
		String bankCodes = Global.getValue("hlb_bank_code");//获取银行编码,多个用逗号分割
		try {
			for (BorrowRepay borrowRepay : borrowRepayList) {

				logger.info("还款记录" + borrowRepay.getId() + "开始还款");

				// 查询用户、用户详情、借款及用户银行卡信息
				User user = cloanUserService.getById(borrowRepay.getUserId());
				//UserBaseInfo baseInfo = userBaseInfoService.findByUserId(borrowRepay.getUserId());
				Borrow borrow = clBorrowService.getById(borrowRepay.getBorrowId());
				BankCard bankCard = bankCardService.getBankCardByUserId(borrowRepay.getUserId());
				if(bankCodes.indexOf(bankCard.getBankCode()) == -1){
					logger.info("还款记录" + borrowRepay.getId() +bankCard.getBank()+"卡不支持代扣");
					continue;
				}
				// 扣款失败无异步通知 故先查询订单是否已经在支付处理中
				Map<String, Object> payLogMap = new HashMap<String, Object>();
				payLogMap.put("userId", borrowRepay.getUserId());
				payLogMap.put("borrowId", borrowRepay.getBorrowId());
				payLogMap.put("type", PayLogModel.TYPE_COLLECT);
				payLogMap.put("scenes", PayLogModel.SCENES_REPAYMENT);
				PayLog repaymentLog = payLogService.findSelectiveOne(payLogMap);
				//查询渠道
				Channel channel = channelService.findById(borrow.getChannelId());
				String url = "";
				if(channel != null){
					if("kn".equals(channel.getCode())){
						url = Global.getValue("notify_url_host");
					}else if("czqb".equals(channel.getCode())){
						url = Global.getValue("app_server_host");
					}else{
						logger.info("未找到借款渠道信息");
						continue;
					}
				}else{
					logger.info("未找到借款渠道信息");
					continue;
				}
				PayService payService = new PayServiceImpl();
				//通知卡牛参数
				Map<String, Object> pMap = new HashMap<String, Object>();
				pMap.put("loanId", String.valueOf(borrow.getId()));// 借款id
				pMap.put("remainBackChance", Global.getInt("kn_repay_limit") - borrow.getRepayCount());// 剩余还款次数
				pMap.put("actualAmount",
						borrowRepay.getAmount() + borrowRepay.getPenaltyAmout() + borrowRepay.getPenaltyManageAmt());// 用户实际还款金额
				pMap.put("lateFees", borrowRepay.getPenaltyManageAmt());
				boolean isNotifyKn = false;
				String payReqTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//请求时间,格式yyyyMMddHHmmss
				// 订单存在并不是支付失败记录
				if (null != repaymentLog
						&& !PayLogModel.STATE_PAYMENT_FAILED.equals(repaymentLog.getState())) {
					if (PayLogModel.STATE_PAYMENT_SUCCESS.equals(repaymentLog.getState())) {
						continue;
					}
					// 查询订单接口
					QueryOrderVo queryOrderVo = new QueryOrderVo();
					queryOrderVo.setP3_orderId(repaymentLog.getOrderNo());
					queryOrderVo.setP4_timestamp(payReqTime);
					Map<String, String> res = payService.queryOrder(queryOrderVo);
					// 订单支付成功状态
					String trade_status = res.get("trade_status");
					if ("SUCCESS".equals(trade_status)) {
					// 查找对应的还款计划
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("id", borrowRepay.getId());
					param.put("repayTime", DateUtil.getNow());
					param.put("repayWay",BorrowRepayLogModel.REPAY_WAY_CHARGE);
					param.put("repayAccount", bankCard.getCardNo());
					param.put("amount", borrowRepay.getAmount());
					param.put("serialNumber", repaymentLog.getOrderNo());
					param.put("penaltyAmout",borrowRepay.getPenaltyAmout());
					param.put("state", "10");
					if (!borrowRepay.getState().equals(BorrowRepayModel.STATE_REPAY_YES)) {
						borrowRepayService.confirmRepay(param);
					}

					// 更新订单状态
					Map<String, Object> payLogParamMap = new HashMap<String, Object>();
					payLogParamMap.put("state",PayLogModel.STATE_PAYMENT_SUCCESS);
					payLogParamMap.put("updateTime", DateUtil.getNow());
					payLogParamMap.put("id", repaymentLog.getId());
					payLogService.updateSelective(payLogParamMap);
					pMap.put("loanStatus", "8");// 借款状态
					pMap.put("backingStatus", "2");// 还款子状态
	 				pMap.put("finishTime", new Date().getTime());//借款结清日期
					isNotifyKn = true;
				}else if ("DOING".equals(trade_status) || "INIT".equals(trade_status)) {
					pMap.put("loanStatus", BorrowModel.STATE_DELAY.equals(borrow.getState()) ? "7" : "6");// 借款状态
					pMap.put("backingStatus", "1");// 还款子状态
					isNotifyKn = true;
				}else if ("FAILED".equals(trade_status)){
					// 更新订单状态
					Map<String, Object> payLogParamMap = new HashMap<String, Object>();
					payLogParamMap.put("state", PayLogModel.STATE_PAYMENT_FAILED);
					payLogParamMap.put("updateTime", DateUtil.getNow());
					payLogParamMap.put("id", repaymentLog.getId());
					payLogService.updateSelective(payLogParamMap);
					pMap.put("loanStatus", BorrowModel.STATE_DELAY.equals(borrow.getState()) ? "7" : "6");// 借款状态
					pMap.put("backingStatus", "3");// 还款子状态
					isNotifyKn = true;
					logger.info("===> 查询交易异常：" + res.get("response_code") + "  " + res.get("response_message"));
				}
				else{
					logger.info("===> 未知异常：" + res.get("response_code") + "  " + res.get("response_message"));
				}
				if(isNotifyKn && "kn".equals(channel.getCode())){
					// 通知卡牛更新状态
					syncLoanInfoKnService.syncLoanInfoKn(pMap,false,"定时还款通知卡牛");
					continue;
				}
			}
				String orderNo = OrderNoUtil.getSerialNumber();
				double amount = BigDecimalUtil.add(borrowRepay.getAmount(), borrowRepay.getPenaltyAmout(),borrowRepay.getPenaltyManageAmt());
				
				if("dev".equals(Global.getValue("app_environment")) || "pre".equals(Global.getValue("app_environment"))){
					amount = 1.51;
				}
				//查询基本信息
				UserBaseInfo ubi = userBaseInfoService.findByUserId(borrowRepay.getUserId());
				//支付请求
				CreateOrderVo createOrderVo = new CreateOrderVo();
				createOrderVo.setP3_orderId(orderNo);
				createOrderVo.setP4_timestamp(payReqTime);
				createOrderVo.setP5_payerName(ubi.getRealName());
				createOrderVo.setP6_idCardType("IDCARD");
				createOrderVo.setP7_idCardNo(ubi.getIdNo());
				createOrderVo.setP8_cardNo(bankCard.getCardNo());
				createOrderVo.setP9_phone(bankCard.getPhone());
				createOrderVo.setP10_currency("CNY");
				createOrderVo.setP11_orderAmount(String.valueOf(amount));
				createOrderVo.setP12_goodsCatalog("100001");
				createOrderVo.setP13_goodsName("银行卡代收");
				createOrderVo.setP14_goodsDesc("银行卡代收");
				createOrderVo.setP15_terminalType("OTHER");
				createOrderVo.setP16_terminalId("001");
				createOrderVo.setP17_orderIp("127.0.0.1");
				createOrderVo.setP18_serverCallbackUrl(url+"/pay/hlb/repaymentNotify.htm");
				
	            PayReqLog payReqLog = new PayReqLog();
				payReqLog.setOrderNo(orderNo);
				payReqLog.setService("合利宝自动代扣");
				payReqLog.setCreateTime(DateUtil.getNow());
				payReqLog.setParams(JSON.toJSONString(createOrderVo));
				payReqLog.setReqDetailParams("");
				payReqLog.setIp("127.0.0.1");
				payReqLogService.writePayLog(payReqLog);
				//支付记录
				Date curDate = new Date();
				payLog = new PayLog();
				payLog.setOrderNo(orderNo);
	    			payLog.setUserId(borrowRepay.getUserId());
	    			payLog.setBorrowId(borrowRepay.getBorrowId());
	    			payLog.setAmount(amount);
	    			//payLog.setAmount(Double.parseDouble());
	    			payLog.setCardNo(bankCard.getCardNo());
	    			payLog.setBank(bankCard.getBank());
	    			payLog.setSource(PayLogModel.SOURCE_FUNDS_OWN);
	    			payLog.setType(PayLogModel.TYPE_COLLECT);
	    			payLog.setScenes(PayLogModel.SCENES_REPAYMENT);
	    			payLog.setRemark("合利宝自动代扣");
	    			payLog.setPayReqTime(curDate);
	    			payLog.setUpdateTime(curDate);
	    			payLog.setCreateTime(curDate);
	    			payLog.setPayPlatform("3");//京东支付平台
	    			payLog.setState(PayLogModel.STATE_PAYMENT_WAIT);
	    			
	    			//交易
	    			Map<String, String> result = payService.trade(createOrderVo);
	    			String trade_status = result.get("trade_status");

	    			if ("SUCCESS".equals(trade_status)) {
                    logger.info("交易成功");
                    Map<String, Object> repayMap = new HashMap<String, Object>();
	 				repayMap.put("userId", payLog.getUserId());
	 				repayMap.put("borrowId", payLog.getBorrowId());
	 				borrowRepay = borrowRepayService.findSelective(repayMap);
	 				bankCard = bankCardService.getBankCardByUserId(payLog.getUserId());
	 			
	 				if (borrowRepay != null) {
	 					Map<String, Object> param = new HashMap<String, Object>();
	 					param.put("id", borrowRepay.getId());
	 					//param.put("repayTime", DateUtil.dateStr4(DateUtil.getNow()));
	 					param.put("repayTime", curDate);
	 					param.put("repayWay", BorrowRepayLogModel.REPAY_WAY_CHARGE);
	 					param.put("repayAccount", bankCard.getCardNo());
	 					param.put("amount", payLog.getAmount());
	 					param.put("serialNumber", payLog.getOrderNo());
	 					param.put("penaltyAmout", borrowRepay.getPenaltyAmout());
	 					param.put("state", "10");
	 					if (!borrowRepay.getState().equals(BorrowRepayModel.STATE_REPAY_YES)) {
	 						   borrowRepayService.confirmRepay(param);
	 					} 
	 				} 
	 				
                    // 更新订单状态
                    payLog.setState(PayLogModel.STATE_PAYMENT_SUCCESS);
                    payLogService.writePayLog(payLog);
                //发送短信通知还款成功
                if("prod".equals(Global.getValue("app_environment"))){
					String platform = "成长钱包";
					if(channel != null){
						platform = channel.getName();
					}
					/* 调用本地发短信的方法*/
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("platform", platform);
					data.put("loan",borrowRepay.getAmount()+borrowRepay.getPenaltyAmout()+borrowRepay.getPenaltyManageAmt());
					data.put("time", DateUtil.dateStr6(borrow.getCreateTime()));
					clSmsService.sendSmsAsyncron("repayInform", data, user.getLoginName());
				}
                pMap.put("loanStatus", "8");// 借款状态
				pMap.put("backingStatus", "2");// 还款子状态
 				pMap.put("finishTime", new Date().getTime());//借款结清日期
 				isNotifyKn = true;
        			//clSmsService.repayInform(borrowRepay.getUserId(), borrowRepay.getBorrowId());
                }else if ("FAILED".equals(trade_status)) {
                	logger.info("交易关闭，交易失败");
                    //TODO 失败后业务逻辑
                		payLog.setState(PayLogModel.STATE_PAYMENT_FAILED);
                		payLogService.writePayLog(payLog);
                		pMap.put("loanStatus", BorrowModel.STATE_DELAY.equals(borrow.getState()) ? "7" : "6");// 借款状态
					pMap.put("backingStatus", "3");// 还款子状态
					isNotifyKn = true;
					logger.info("===> 交易异常：" + result.get("response_code") + "  " + result.get("response_message"));
                }else if ("INIT".equals(trade_status) || "DOING".equals(trade_status)) {
                	logger.info("等待支付结果，处理中");//需查询交易获取结果或等待通知结果
                		payLog.setState(PayLogModel.STATE_PAYMENT_WAIT);
                		payLogService.writePayLog(payLog);	
                		pMap.put("loanStatus", BorrowModel.STATE_DELAY.equals(borrow.getState()) ? "7" : "6");// 借款状态
					pMap.put("backingStatus", "1");// 还款子状态
					isNotifyKn = true;
                }
                else{
					logger.info("===> 未知异常：" + result.get("response_code") + "  " + result.get("response_message"));
				}
				if(isNotifyKn&&"kn".equals(channel.getCode())){
					// 通知卡牛更新状态
					syncLoanInfoKnService.syncLoanInfoKn(pMap,false,"定时还款通知卡牛");
				}
			}
			succeed++;
			total++;
		} catch (Exception e) {
			fail++;
			total++;
			payLogService.writePayLog(payLog);
			logger.error(e.getMessage(),e);
		}
		
		logger.info("当天自动扣款完成...");
		quartzRemark = "执行总次数"+total+",成功"+succeed+"次,失败"+fail+"次";
		return quartzRemark;

	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		QuartzInfoService quartzInfoService = (QuartzInfoService) BeanUtil
				.getBean("quartzInfoService");
		QuartzLogService quartzLogService = (QuartzLogService) BeanUtil
				.getBean("quartzLogService");
		// 查询当前任务信息
		QuartzInfo quartzInfo = quartzInfoService.findByCode("doRepaymentToday1");
		Map<String, Object> qiData = new HashMap<>();
		qiData.put("id", quartzInfo.getId());

		QuartzLog quartzLog = new QuartzLog();
		quartzLog.setQuartzId(quartzInfo.getId());
		quartzLog.setStartTime(DateUtil.getNow());
		try {
			
			String remark = repayment();
			
			quartzLog.setTime(DateUtil.getNow().getTime() - quartzLog.getStartTime().getTime());
			quartzLog.setResult("10");
			quartzLog.setRemark(remark);
			qiData.put("succeed", quartzInfo.getSucceed() + 1);
		} catch (Exception e) {

			quartzLog.setResult("20");
			qiData.put("fail", quartzInfo.getFail() + 1);
			logger.error(e.getMessage(), e);

		} finally {
			logger.info("保存定时任务日志");

			quartzLogService.save(quartzLog);
			quartzInfoService.update(qiData);
		}

	}
}