package com.rongdu.cashloan.manage.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tool.util.DateUtil;
import tool.util.StringUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.BorrowProgress;
import com.rongdu.cashloan.cl.domain.PayLog;
import com.rongdu.cashloan.cl.model.ManagePayLogModel;
import com.rongdu.cashloan.cl.model.PayLogModel;
import com.rongdu.cashloan.cl.service.BorrowProgressService;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.FddContractRecordService;
import com.rongdu.cashloan.cl.service.PayLogService;
import com.rongdu.cashloan.cl.service.ProfitAmountService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.model.BorrowModel;

 /**
 * 支付记录Controller
 * 
 * @author gc
 * @version 1.0.0
 * @date 2017-03-07 16:18:56
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Scope("prototype")
@Controller
public class ManagePayLogController extends ManageBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(ManagePayLogController.class);
	
	@Resource
	private PayLogService payLogService;
	@Resource
	private ClBorrowService clBorrowService;
	@Resource
	private BorrowProgressService borrowProgressService;
	@Resource
	private BorrowRepayService borrowRepayService;
	@Resource
	private ProfitAmountService profitAmountService;

	@Resource
	private FddContractRecordService fddContractRecordService;
	/**
	 * 支付记录列表页查看
	 * 
	 * @param searchParams
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/pay/log/page.htm")
	public void page(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {

		Map<String, Object> searchMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(search)) {
			searchMap = JsonUtil.parse(search, Map.class);
		}
		String type  = StringUtil.isNull(searchMap.get("type"));
		String[] typeArray = type.split(",");
		
		List<String> typeList =  new ArrayList<String>();
		for (String typeStr : typeArray) {
			if (StringUtil.isNotBlank(typeStr)) {
				typeList.add(typeStr);
			}
		}
		searchMap.put("type", typeList);

		Page<ManagePayLogModel> page = payLogService.page(current, pageSize,searchMap);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response, result);
	}

	/**
	 * 支付记录详情查看
	 * 
	 * @param id
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/pay/log/findDetail.htm")
	public void findDetail(@RequestParam(value = "id") Long id)
			throws Exception {

		ManagePayLogModel model = payLogService.findDetail(id);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, model);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response, result);
	}

	/**
	 * 审核
	 * 
	 * @param id
	 */
	@RequestMapping(value = "/modules/manage/pay/log/auditPay.htm", method = RequestMethod.POST)
	public void audit(@RequestParam(value = "id") Long id,@RequestParam(value = "state") String state) {
		
		/*boolean flag = true;
		String msg = Constant.OPERATION_FAIL;

		Map<String, Object> checkMap = payLogService.checkPayLogState(id,state);
		
		if (checkMap.isEmpty()) {
			flag = payLogService.auditPay(id,state);
		}
		
		long borrowId = payLogService.findBorrowId(id);
		String orderNo = borrowProgressService.findOrderNo(borrowId);
		JSONObject object = fddContractRecordService.generateContract(orderNo, "10");
		String res = object.getString(Constant.RESPONSE_CODE);
		Map<String, Object> result = new HashMap<String, Object>();
		if(res.equals("200")){
			logger.info((object.get("data").toString()));
			JSONObject data = (JSONObject)(object.get("data"));
			String contractNo = data.getString("contractNo");
			object = fddContractRecordService.signAutoContract(contractNo);
			res = object.getString(Constant.RESPONSE_CODE);
			if(res.equals("200")){
				if (flag) {
					result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
					result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
				} else {
					result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
					result.put(Constant.RESPONSE_CODE_MSG, msg);
				}
			}else{
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, msg);
			}
		}*/
		boolean flag = true;
		String msg = Constant.OPERATION_FAIL;

		Map<String, Object> checkMap = payLogService.checkPayLogState(id,state);
		
		if (checkMap.isEmpty()) {
			flag = payLogService.auditPay(id,state);
		}
		else{
			ServletUtils.writeToResponse(response, checkMap);
			return;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		if (flag) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, msg);
		}
		ServletUtils.writeToResponse(response, result);

	}

	
	/**
	 * 模拟付款成功 - 异步回调
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/pay/testPayNotify.htm", method = RequestMethod.POST)
	public void testPaymentNotify(@RequestParam(value = "orderNo") String orderNo) throws Exception {

		PayLog payLog = payLogService.findByOrderNo(orderNo);
		String codeMsg = null;
		if(null  == payLog ){
			logger.warn("未查询到对应的支付订单");
			codeMsg ="未查询到对应的支付订单";
			return ;
		}
		if (PayLogModel.STATE_PAYMENT_WAIT.equals(payLog.getState()) || 
				PayLogModel.STATE_AUDIT_PASSED.equals(payLog.getState()) || 
				PayLogModel.STATE_PAYMENT_FAILED.equals(payLog.getState())) {
				if(PayLogModel.SCENES_LOANS.equals(payLog.getScenes())){
					// 修改借款状态
					Map<String, Object> map = new HashMap<>();
					map.put("id", payLog.getBorrowId());
					map.put("state", BorrowModel.STATE_REPAY);
					clBorrowService.updatePayState(map);
	
					// 放款进度添加
					BorrowProgress bp = new BorrowProgress();
					bp.setUserId(payLog.getUserId());
					bp.setBorrowId(payLog.getBorrowId());
					bp.setState(BorrowModel.STATE_REPAY);
					bp.setRemark(BorrowModel.convertBorrowRemark(bp.getState()));
					bp.setCreateTime(DateUtil.getNow());
					borrowProgressService.save(bp);
	
					Borrow borrow = clBorrowService.getById(payLog.getBorrowId());
					
					// 生成还款计划并授权
					borrowRepayService.genRepayPlan(borrow);
					// 更新订单状态
					Map<String,Object> paramMap = new HashMap<String, Object>();
					paramMap.put("state", PayLogModel.STATE_PAYMENT_SUCCESS);
					paramMap.put("updateTime",DateUtil.getNow());
					paramMap.put("id",payLog.getId());
					payLogService.updateSelective(paramMap);
	
				}else if(PayLogModel.SCENES_PROFIT.equals(payLog.getScenes())){
					// 更新取现金额， 添加取现记录
					profitAmountService.cash(payLog.getUserId(), payLog.getAmount());

					// 更新订单状态
					Map<String,Object> paramMap = new HashMap<String, Object>();
					paramMap.put("state", PayLogModel.STATE_PAYMENT_SUCCESS);
					paramMap.put("updateTime",DateUtil.getNow());
					paramMap.put("id",payLog.getId());
					payLogService.updateSelective(paramMap);
				} 
				
		}else{
			codeMsg = "订单状态错误";
		}

		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtil.isBlank(codeMsg)) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, codeMsg);
		}
		ServletUtils.writeToResponse(response, result);
	}
	
}
