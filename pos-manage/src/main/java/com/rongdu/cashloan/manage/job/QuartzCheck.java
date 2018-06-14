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

import com.rongdu.cashloan.cl.domain.PayCheck;
import com.rongdu.cashloan.cl.domain.PayLog;
import com.rongdu.cashloan.cl.model.PayCheckModel;
import com.rongdu.cashloan.cl.model.PayLogModel;
import com.rongdu.cashloan.cl.model.pay.lianlian.PaymentCheckModel;
import com.rongdu.cashloan.cl.model.pay.lianlian.TransactionCheckModel;
import com.rongdu.cashloan.cl.model.pay.lianlian.constant.LianLianConstant;
import com.rongdu.cashloan.cl.model.pay.lianlian.util.ReadFileUtil;
import com.rongdu.cashloan.cl.service.PayCheckService;
import com.rongdu.cashloan.cl.service.PayLogService;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.util.DateUtil;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;

/**
 * 自动对账
 * 
 * @author gc
 * @version 1.0.0
 * @date 2017年4月16日 下午3:18:12
 * Copyright 杭州融都科技股份有限公司 All Rights Reserved
 * 官方网站：www.erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Component
@Lazy(value = false)
public class QuartzCheck implements Job {

	private static final Logger logger = Logger.getLogger(QuartzCheck.class);

	/**
	 *  付款对账
	 * @return
	 */
	public void paymentCheck(){
		logger.info("进入实时付款交易对账...");
		PayCheckService payCheckService  = (PayCheckService)BeanUtil.getBean("payCheckService");
		PayLogService payLogService  = (PayLogService)BeanUtil.getBean("payLogService");

		String merchartNo = Global.getValue(LianLianConstant.BUSINESS_NO);
		Date date = DateUtil.rollDay(DateUtil.getNow(),LianLianConstant.CHECK_DAY);
		String dateStr =  DateUtil.dateStr7(date);
		List<String> list = ReadFileUtil.getFile(LianLianConstant.CHECK_PREFIX_PAY + merchartNo + "_" + dateStr + ".txt");
		
		// 查询对账日期请求记录
		Date startTime = DateUtil.getDayStartTime(date);
		Date endTime  = DateUtil.getDayEndTime(date);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("type", PayLogModel.TYPE_PAYMENT);
		List<PayLog> payLogList = payLogService.findCheckList(paramMap);
		
		// 以连连对账文件为准 进行开始对账
		String merchantNo = Global.getValue(LianLianConstant.BUSINESS_NO);
		for (String string : list) {
			PaymentCheckModel model = new PaymentCheckModel();
			model = model.anlsStr(string);
			if (!merchantNo.equals(model.getOid_partner())) {
				logger.info("商户号不匹配,对账商户号：" + model.getOid_partner());
				continue;
			}
			boolean mismatch = true;
			for (PayLog payLog : payLogList) {
				if (model.getOrderNo().equals(payLog.getOrderNo())) {
					mismatch = false;
					if("0".equals(model.getState()) && payLog.getState().equals(PayLogModel.STATE_PAYMENT_SUCCESS) ){
						if(model.getAmount() != payLog.getAmount()){
							PayCheck check = new PayCheck(
									model.getOrderNo(),
									payLog.getAmount(),
									model.getAmount(),
									PayCheckModel.TYPE_AMOUNT_MISMATCH,
									PayCheckModel.PROCESS_RESULT_PENDING_TREATMENT);
							check.setState(model.getState());
							payCheckService.save(check);
						}
					}else{
						PayCheck check = new PayCheck(model.getOrderNo(),
								payLog.getAmount(), model.getAmount(),
								PayCheckModel.TYPE_AMOUNT_MISMATCH,
								PayCheckModel.PROCESS_RESULT_PENDING_TREATMENT);
						check.setState(model.getState());
						payCheckService.save(check);
					}
				}
			}
			if (mismatch) {
				PayCheck check = new PayCheck(model.getOrderNo(), 0.0,
						model.getAmount(),
						PayCheckModel.TYPE_UNILATERAL_PAYMENT,
						PayCheckModel.PROCESS_RESULT_PENDING_TREATMENT);
				check.setState(model.getState());
				payCheckService.save(check);
			}
		}
		
		// 以支付记录为准 进行开始对账
		for (PayLog payLog : payLogList) {
			boolean mismatch = true;
			PaymentCheckModel model = new PaymentCheckModel();
			for (String string : list) {
				model = model.anlsStr(string);
				if (payLog.getOrderNo().equals(model.getOrderNo())) {
					mismatch = false;
				}else{
					
				}
			}
			if (mismatch) {
				PayCheck check = new PayCheck(payLog.getOrderNo(),
						payLog.getAmount(), 0.0,
						PayCheckModel.TYPE_UNILATERAL_OUR,
						PayCheckModel.PROCESS_RESULT_PENDING_TREATMENT);
				check.setState(model.getState());
				payCheckService.save(check);
			}
		}
		
	}
	
	/**
	 * 交易对账(分期付对账)
	 */
	public void transactionCheck(){
		logger.info("进入分期付交易对账...");
		PayCheckService payCheckService  = (PayCheckService)BeanUtil.getBean("payCheckService");
		PayLogService payLogService  = (PayLogService)BeanUtil.getBean("payLogService");

		String merchartNo = Global.getValue(LianLianConstant.BUSINESS_NO);
		Date date = DateUtil.rollDay(DateUtil.getNow(),LianLianConstant.CHECK_DAY);
		String dateStr =  DateUtil.dateStr7(date);
		List<String> list = ReadFileUtil.getFile(LianLianConstant.CHECK_PREFIX_CHARGE + merchartNo + "_" + dateStr + ".txt");
		
		// 查询对账日期请求记录
		Date startTime = DateUtil.getDayStartTime(date);
		Date endTime  = DateUtil.getDayEndTime(date);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("type", PayLogModel.TYPE_COLLECT);
		List<PayLog> payLogList = payLogService.findCheckList(paramMap);
	
		
		// 以连连对账文件为准 进行开始对账
		String merchantNo = Global.getValue(LianLianConstant.BUSINESS_NO);
		for (String string : list) {
			TransactionCheckModel model = new TransactionCheckModel();
			model = model.anlsStr(string);
			if (!merchantNo.equals(model.getOid_partner())) {
				logger.info("商户号不匹配,对账商户号：" + model.getOid_partner());
				continue;
			}
			boolean mismatch = true;
			for (PayLog payLog : payLogList) {
				if (model.getOrderNo().equals(payLog.getOrderNo())) {
					mismatch = false;
					if ("0".equals(model.getState())
							&& payLog.getState().equals(
									PayLogModel.STATE_PAYMENT_SUCCESS)) {
						if (model.getAmount() != payLog.getAmount()) {
							PayCheck check = new PayCheck(model.getOrderNo(),
									payLog.getAmount(),
									model.getAmount(),
									PayCheckModel.TYPE_AMOUNT_MISMATCH,
									PayCheckModel.PROCESS_RESULT_PENDING_TREATMENT);
							check.setState(model.getState());
							payCheckService.save(check);
						}
					} else {
						PayCheck check = new PayCheck(model.getOrderNo(),
								payLog.getAmount(), model.getAmount(),
								PayCheckModel.TYPE_AMOUNT_MISMATCH,
								PayCheckModel.PROCESS_RESULT_PENDING_TREATMENT);
						check.setState(model.getState());
						payCheckService.save(check);
					}
				}
			}
			if (mismatch) {
				PayCheck check = new PayCheck(model.getOrderNo(), 0.0,
						model.getAmount(),
						PayCheckModel.TYPE_UNILATERAL_PAYMENT,
						PayCheckModel.PROCESS_RESULT_PENDING_TREATMENT);
				check.setState(model.getState());
				payCheckService.save(check);
			}
		}
		
		// 以支付记录为准 进行开始对账
		for (PayLog payLog : payLogList) {
			boolean mismatch = true;
			TransactionCheckModel model = new TransactionCheckModel();
			for (String string : list) {
				model = model.anlsStr(string);
				if (payLog.getOrderNo().equals(model.getOrderNo())) {
					mismatch = false;
				}else{
					
				}
			}
			if (mismatch) {
				PayCheck check = new PayCheck(payLog.getOrderNo(),payLog.getAmount(), 0.0,PayCheckModel.TYPE_UNILATERAL_OUR,
						PayCheckModel.PROCESS_RESULT_PENDING_TREATMENT);
				check.setState(model.getState());
				payCheckService.save(check);
			}
		}
		
		
	}
	

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		QuartzInfoService quartzInfoService = (QuartzInfoService) BeanUtil.getBean("quartzInfoService");
		QuartzLogService quartzLogService = (QuartzLogService) BeanUtil.getBean("quartzLogService");
		// 查询当前任务信息
		QuartzInfo quartzInfo = quartzInfoService.findByCode("doPayCheck");
		Map<String, Object> qiData = new HashMap<>();
		qiData.put("id", quartzInfo.getId());

		QuartzLog quartzLog = new QuartzLog();
		quartzLog.setQuartzId(quartzInfo.getId());
		quartzLog.setStartTime(DateUtil.getNow());
		try {
			
			// 付款对账
			paymentCheck();

			// 交易对账
			transactionCheck();

			quartzLog.setTime(DateUtil.getNow().getTime() - quartzLog.getStartTime().getTime());
			quartzLog.setResult("10");
			quartzLog.setRemark("执行成功");
			qiData.put("succeed", quartzInfo.getSucceed() + 1);
		} catch (Exception e) {
			quartzLog.setResult("20");
			quartzLog.setRemark("执行出现失败");
			qiData.put("fail", quartzInfo.getFail() + 1);
			logger.error(e.getMessage(), e);
		} finally {
			logger.info("保存定时任务日志");
			quartzLogService.save(quartzLog);
			quartzInfoService.update(qiData);
		}

	}
}