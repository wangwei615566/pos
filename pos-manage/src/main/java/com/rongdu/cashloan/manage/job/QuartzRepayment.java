package com.rongdu.cashloan.manage.job;

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

import tool.util.BeanUtil;
import tool.util.BigDecimalUtil;
import tool.util.DateUtil;
import tool.util.StringUtil;

import com.alibaba.fastjson.JSONObject;
import com.rongdu.cashloan.cl.domain.BankCard;
import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.domain.PayLog;
import com.rongdu.cashloan.cl.model.BorrowRepayLogModel;
import com.rongdu.cashloan.cl.model.BorrowRepayModel;
import com.rongdu.cashloan.cl.model.PayLogModel;
import com.rongdu.cashloan.cl.model.pay.lianlian.QueryRepaymentModel;
import com.rongdu.cashloan.cl.model.pay.lianlian.RepaymentModel;
import com.rongdu.cashloan.cl.model.pay.lianlian.RiskItems;
import com.rongdu.cashloan.cl.model.pay.lianlian.constant.LianLianConstant;
import com.rongdu.cashloan.cl.model.pay.lianlian.util.LianLianHelper;
import com.rongdu.cashloan.cl.service.BankCardService;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.ClSmsService;
import com.rongdu.cashloan.cl.service.PayLogService;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.common.util.OrderNoUtil;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.domain.User;
import com.rongdu.cashloan.core.domain.UserBaseInfo;
import com.rongdu.cashloan.core.service.CloanUserService;
import com.rongdu.cashloan.core.service.UserBaseInfoService;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;

/**
 * 自动扣款还款
 * 
 * @author gc
 * @version 1.0.0
 * @date 2017年3月21日 下午3:28:37 
 * Copyright 杭州融都科技股份有限公司 All Rights Reserved
 * 官方网站：www.erongdu.com  
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Component
@Lazy(value = false)
public class QuartzRepayment implements Job {

	private static final Logger logger = Logger.getLogger(QuartzRepayment.class);

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

		// 查询待还款记录
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("repayTime", DateUtil.getNow());
		paramMap.put("state", BorrowRepayModel.STATE_REPAY_NO);
		List<BorrowRepay> borrowRepayList = borrowRepayService.findUnRepay(paramMap);

		logger.info("进入自动扣款还款...");
		String quartzRemark = null;
		int succeed = 0;
		int fail = 0;
		int total = 0;
		try {
			for (BorrowRepay borrowRepay : borrowRepayList) {

				logger.info("还款记录" + borrowRepay.getId() + "开始还款");

				// 查询用户、用户详情、借款及用户银行卡信息
				User user = cloanUserService.getById(borrowRepay.getUserId());
				UserBaseInfo baseInfo = userBaseInfoService.findByUserId(borrowRepay.getUserId());
				Borrow borrow = clBorrowService.getById(borrowRepay.getBorrowId());
				BankCard bankCard = bankCardService.getBankCardByUserId(borrowRepay.getUserId());

				// 扣款失败无异步通知 故先查询订单是否已经在支付处理中
				Map<String, Object> payLogMap = new HashMap<String, Object>();
				payLogMap.put("userId", borrowRepay.getUserId());
				payLogMap.put("borrowId", borrowRepay.getBorrowId());
				payLogMap.put("type", PayLogModel.TYPE_COLLECT);
				payLogMap.put("scenes", PayLogModel.SCENES_REPAYMENT);
				PayLog repaymentLog = payLogService.findSelectiveOne(payLogMap);

				// 订单存在并不是支付失败记录
				if (null != repaymentLog
						&& !PayLogModel.STATE_PAYMENT_FAILED.equals(repaymentLog.getState())) {
					if (PayLogModel.STATE_PAYMENT_SUCCESS.equals(repaymentLog.getState())) {
						continue;
					}

					String orderNo = OrderNoUtil.getSerialNumber();
					QueryRepaymentModel queryRepayment = new QueryRepaymentModel(orderNo);
					queryRepayment.setNo_order(repaymentLog.getOrderNo());
					queryRepayment.setDt_order(DateUtil.dateStr3(repaymentLog.getPayReqTime()));
					LianLianHelper helper = new LianLianHelper();
					queryRepayment = (QueryRepaymentModel) helper.queryRepayment(queryRepayment);

					if (queryRepayment.checkReturn()
							&& LianLianConstant.RESULT_SUCCESS.equals(queryRepayment.getResult_pay())) {
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
						continue;
					}else if (queryRepayment.checkReturn()
							&& LianLianConstant.RESULT_PROCESSING.equals(queryRepayment.getResult_pay())) {
						continue;
					}else{
						// 更新订单状态
						Map<String, Object> payLogParamMap = new HashMap<String, Object>();
						payLogParamMap.put("state",PayLogModel.STATE_PAYMENT_FAILED);
						payLogParamMap.put("updateTime", DateUtil.getNow());
						payLogParamMap.put("id", repaymentLog.getId());
						payLogService.updateSelective(payLogParamMap);
					}
				}

				Date payReqTime = DateUtil.getNow();
				String orderNo = OrderNoUtil.getSerialNumber();
				RepaymentModel repayment = new RepaymentModel(orderNo);
				repayment.setUser_id(user.getUuid());
				repayment.setBusi_partner(LianLianConstant.GOODS_VIRTUAL);
				repayment.setDt_order(DateUtil.dateStr3(payReqTime));
				repayment.setName_goods("还款" + borrow.getOrderNo());
				repayment.setInfo_order("repayment_" + borrow.getOrderNo());
				
				double amount = BigDecimalUtil.add(borrowRepay.getAmount(),borrowRepay.getPenaltyAmout());  //计算实际还款金额
				if ("dev".equals(Global.getValue("app_environment")) || "pre".equals(Global.getValue("app_environment"))) {
					repayment.setMoney_order("0.01");
				} else {
					repayment.setMoney_order(StringUtil.isNull(amount));
				}
				
				repayment.setAmount(amount);
				RiskItems riskItems = new RiskItems();
				riskItems.setFrms_ware_category("2010");
				riskItems.setUser_info_mercht_userno(user.getUuid());
				riskItems.setUser_info_bind_phone(baseInfo.getPhone());
				riskItems.setUser_info_dt_register(DateUtil.dateStr3(user.getRegistTime()));
				riskItems.setUser_info_full_name(baseInfo.getRealName());
				riskItems.setUser_info_id_no(baseInfo.getIdNo());
				riskItems.setUser_info_identify_type("1");
				riskItems.setUser_info_identify_state("1");
				repayment.setRisk_item(JSONObject.toJSONString(riskItems));
				repayment.setSchedule_repayment_date(DateUtil.dateStr2(borrowRepay.getRepayTime()));
				repayment.setRepayment_no(borrow.getOrderNo());
				repayment.setNo_agree(bankCard.getAgreeNo());
				repayment.setNotify_url(Global.getValue("notify_url_host")
						+ "/pay/lianlian/repaymentNotify.htm");

				logger.info("Borrow" + borrow.getOrderNo() + "进行还款,还款金额" + repayment.getMoney_order());

				LianLianHelper helper = new LianLianHelper();
				repayment = (RepaymentModel) helper.repayment(repayment);

				PayLog payLog = new PayLog();
				payLog.setOrderNo(repayment.getOrderNo());
				payLog.setUserId(borrowRepay.getUserId());
				payLog.setBorrowId(borrowRepay.getBorrowId());
				payLog.setAmount(repayment.getAmount());
				payLog.setCardNo(bankCard.getCardNo());
				payLog.setBank(bankCard.getBank());
				payLog.setSource(PayLogModel.SOURCE_FUNDS_OWN);
				payLog.setType(PayLogModel.TYPE_COLLECT);
				payLog.setScenes(PayLogModel.SCENES_REPAYMENT);
				payLog.setState(PayLogModel.STATE_PAYMENT_WAIT);
				payLog.setRemark(repayment.getRet_msg());
				payLog.setPayReqTime(payReqTime);
				payLog.setCreateTime(DateUtil.getNow());
				payLogService.save(payLog);
				
				clSmsService.repayInform(borrowRepay.getUserId(), borrowRepay.getBorrowId());
			}
			succeed++;
			total++;
		} catch (Exception e) {
			fail++;
			total++;
			logger.error(e.getMessage(),e);
		}
		
		logger.info("自动扣款完成...");
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
		QuartzInfo quartzInfo = quartzInfoService.findByCode("doRepayment");
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