package com.rongdu.cashloan.manage.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.rongdu.cashloan.cl.domain.BorrowDetail;
import com.rongdu.cashloan.cl.domain.BorrowProgress;
import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.domain.Channel;
import com.rongdu.cashloan.cl.domain.UrgeRepayOrder;
import com.rongdu.cashloan.cl.model.BorrowRepayModel;
import com.rongdu.cashloan.cl.model.UrgeRepayOrderModel;
import com.rongdu.cashloan.cl.service.BorrowDetailService;
import com.rongdu.cashloan.cl.service.BorrowProgressService;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ChannelService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.LoanDataService;
import com.rongdu.cashloan.cl.service.UrgeRepayOrderService;
import com.rongdu.cashloan.cl.service.UserContactsService;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.cashloan.core.service.UserBaseInfoService;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;

import tool.util.BeanUtil;
import tool.util.BigDecimalUtil;
import tool.util.DateUtil;
import tool.util.StringUtil;


@Component
@Lazy(value = false)
public class QuartzLate implements Job{
	
	private static final Logger logger = Logger.getLogger(QuartzLate.class);
	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	/**
	 * 定时计算逾期
	 * @throws ServiceException 
	 */
	public String late() throws ServiceException {
		BorrowRepayService borrowRepayService = (BorrowRepayService)BeanUtil.getBean("borrowRepayService");
		BorrowProgressService borrowProgressService = (BorrowProgressService)BeanUtil.getBean("borrowProgressService");
		ClBorrowService clBorrowService = (ClBorrowService)BeanUtil.getBean("clBorrowService");
		UrgeRepayOrderService urgeRepayOrderService = (UrgeRepayOrderService)BeanUtil.getBean("urgeRepayOrderService");
		ChannelService channelService = (ChannelService) BeanUtil.getBean("channelService");
		BorrowDetailService borrowDetailService = (BorrowDetailService) BeanUtil.getBean("borrowDetailService");
		UserContactsService userContactsService = (UserContactsService) BeanUtil.getBean("clUserContactsService");
		UserBaseInfoService userBaseInfoService = (UserBaseInfoService) BeanUtil.getBean("userBaseInfoService");	
		final LoanDataService loanDataService = (LoanDataService) BeanUtil.getBean("loanDataService");	
		logger.info("进入逾期计算...");
		String quartzRemark = null;
		int succeed = 0;
		int fail = 0;
		int total = 0;
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("state",BorrowRepayModel.STATE_REPAY_NO);
		List<BorrowRepay> list = borrowRepayService.listSelective(paramMap);
		String borrowDay = Global.getValue("borrow_day");//借款天数
		String[] days = borrowDay.split(",");
		String penaltyFee = Global.getValue("penalty_fee");//逾期费率
		String[] penaltyFees = penaltyFee.split(",");
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				try {
					long day = DateUtil.daysBetween(new Date(), list.get(i).getRepayTime());//计算还款日期和当前日期相差天数
					//判断今天有没有跑过逾期(计算的逾期天数大于贷款已逾期天数,说明今天没有跑逾期)
					if (Math.abs(day)>Integer.parseInt(list.get(i).getPenaltyDay())) {
						if (day<0) {
							Borrow borrow = clBorrowService.findByPrimary(list.get(i).getBorrowId());
							//查询渠道
							Channel channel = channelService.findById(borrow.getChannelId());
							//如果还款时,遇到非余额不足导致失败,不计逾期
							if(borrow.getInsufficientBalance() != null && borrow.getInsufficientBalance() == 0){
								continue;
							}
							Double penaltyManageAmt = list.get(i).getPenaltyManageAmt() == null ? 0 : list.get(i).getPenaltyManageAmt();
							Double penaltyAmt = list.get(i).getPenaltyAmout() == null ? 0 : list.get(i).getPenaltyAmout();
							//如果罚息和本金相等,不继续跑罚息
							if((penaltyManageAmt + penaltyAmt) >= borrow.getAmount() ){
								continue;
							}
							
							double amout = 0.0;
							BorrowDetail borrowDetail = borrowDetailService.findByBorrowId(borrow.getId());
							BorrowRepay br = new BorrowRepay();
							if (borrowDetail != null) {
								amout = BigDecimalUtil.decimal(BigDecimalUtil.mul(borrow.getAmount(), borrowDetail.getOverduePenaltyRatio()),2);
								br.setPenaltyManageAmt(borrowDetail.getOverduePenalty());
							} else { // 兼容旧的卡牛
								
								//借款天数与逾期利率对应
								if(channel != null && "kn".equals(channel.getCode())){
									double thridPanaltyFee = Global.getDouble(channel.getCode()+"_penalty_fee");//第三方平台费率
									amout = BigDecimalUtil.decimal(BigDecimalUtil.mul(borrow.getAmount(), thridPanaltyFee),2);
								}else{
									for (int j = 0; j < days.length; j++) {
										if (days[j].equals(borrow.getTimeLimit())) {
											amout = BigDecimalUtil.decimal(
													BigDecimalUtil.mul(borrow.getAmount(), Double.parseDouble(penaltyFees[j])),
													2);
										}
									}
								}
								br.setPenaltyManageAmt(Global.getDouble("kn_penalty_manage_fee"));
							}
							br.setId(list.get(i).getId());
							br.setBorrowId(list.get(i).getBorrowId());
							br.setPenaltyAmout(BigDecimalUtil.decimal(BigDecimalUtil.add(amout,list.get(i).getPenaltyAmout()), 2));
							br.setPenaltyDay(Long.toString(Math.abs(day)));
							
							try{
								//计算逾期11天用户加入黑名单
								if(Math.abs(day) > 10){
									 userBaseInfoService.updateState(list.get(i).getUserId(),"10","逾期11天及以上","逾期定时任务执行");
								}     
							} catch(Exception e){
								logger.error(e.getMessage(),e);
							}
							logger.info("id--" + list.get(i).getId() + " ==> 已经逾期 " + Math.abs(day) + " 天,逾期费用 " + amout + "元");
							int msg  = borrowRepayService.updateLate(br);
							if (msg>0) {
								//保存逾期进度
								Map<String, Object> map = new HashMap<>();
								map.put("borrowId", list.get(i).getBorrowId());
								List<BorrowProgress> bpList = borrowProgressService.listSeletetiv(map);
								boolean is = true;
								for (BorrowProgress borrowProgress : bpList) {
									if (borrowProgress.getState().equals(BorrowModel.STATE_BAD)
											||borrowProgress.getState().equals(BorrowModel.STATE_DELAY)) {
										is = false;
										break;
									}
								}
								if (is) {
									logger.info("---------添加逾期进度---------");
									BorrowProgress bp = new BorrowProgress();
									bp.setBorrowId(list.get(i).getBorrowId());
									bp.setCreateTime(new Date());
									bp.setRemark("您已逾期,请尽快还款");
									bp.setState(BorrowModel.STATE_DELAY);
									bp.setUserId(list.get(i).getUserId());
									borrowProgressService.save(bp);
									
									Map<String,Object> data = new HashMap<>();
									data.put("id", list.get(i).getBorrowId());
									data.put("state",BorrowModel.STATE_DELAY);
									msg = clBorrowService.updateSelective(data);
									logger.info("---------添加逾期结束---------");
									
								}
								//资产逾期通知
								String assetFundAvailable = Global.getValue("asset_fund_available"); // 资产系统对接开关
								if ("1".equals(assetFundAvailable)) {
									final BorrowRepay borrowRepay = list.get(i);
									borrowRepay.setPenaltyAmout(br.getPenaltyAmout());
									borrowRepay.setPenaltyManageAmt(br.getPenaltyManageAmt());
									borrowRepay.setPenaltyDay(br.getPenaltyDay());
									cachedThreadPool.execute(new Runnable() {
										public void run() {
											loanDataService.loanOverdue(borrowRepay);
										}
									});
								}
								//催收计划
								logger.info("---------修改催收计划start-------");
								UrgeRepayOrder uro =  urgeRepayOrderService.findByBorrowId(list.get(i).getBorrowId());
								if (StringUtil.isBlank(uro)) {
									urgeRepayOrderService.saveOrder(list.get(i).getBorrowId());
								}else {
									Map<String,Object> uroMap = new HashMap<>();
									uroMap.put("penaltyAmout", br.getPenaltyAmout());
									uroMap.put("penaltyDay", br.getPenaltyDay());
									uroMap.put("id", uro.getId());
									//逾期的催收订单第二天继续让催收系统拉取
									uroMap.put("notifyCollection", '0');
									uroMap.put("level", UrgeRepayOrderModel.getLevelByDay(Long.valueOf(br.getPenaltyDay())));
									msg = urgeRepayOrderService.updateLate(uroMap);
								}
								logger.info("---------修改催收计划end-------");
							}else {
								logger.error("定时计算逾期任务修改数据失败");
							}
							if(channel != null && "kn".equals(channel.getCode())){
								//逾期重新获取通讯录
								userContactsService.saveKnContacts(borrow.getUserId());
							}
						}
					}
					succeed++;
					total++;
				} catch (Exception e) {
					fail ++;
					total++;
					logger.error(e.getMessage(),e);
				}
			}
		}
			
		logger.info("逾期计算结束...");
		quartzRemark = "执行总次数"+total+",成功"+succeed+"次,失败"+fail+"次";
		return quartzRemark;
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		QuartzInfoService quartzInfoService = (QuartzInfoService)BeanUtil.getBean("quartzInfoService");
		QuartzLogService quartzLogService = (QuartzLogService)BeanUtil.getBean("quartzLogService");
		QuartzLog ql = new QuartzLog();
		Map<String,Object> qiData = new HashMap<>();
		QuartzInfo qi = quartzInfoService.findByCode("doLate");
		try {
			qiData.put("id", qi.getId());
			ql.setQuartzId(qi.getId());
			ql.setStartTime(DateUtil.getNow());
			
			String remark = late();
			
			ql.setTime(DateUtil.getNow().getTime()-ql.getStartTime().getTime());
			ql.setResult("10");
			ql.setRemark(remark);
			qiData.put("succeed", qi.getSucceed()+1);
			
		}catch (Exception e) {
			ql.setResult("20");
			qiData.put("fail", qi.getFail()+1);
			logger.error(e.getMessage(),e);
		}finally{
			logger.info("保存定时任务日志");
			quartzLogService.save(ql);
			quartzInfoService.update(qiData);
		}
	}
	/*public static void main(String[] args){
		double a = 22.40;
		double b = 11.20;
		System.out.println(BigDecimalUtil.add(a,b,2));
		//BigDecimalUtil.add(22.4,BigDecimalUtil.decimal(BigDecimalUtil.mul(1600, 0.007),2));
		System.out.println(BigDecimalUtil.decimal(BigDecimalUtil.add(22.4,BigDecimalUtil.decimal(BigDecimalUtil.mul(1600, 0.007),2)),2));
	}*/
}
