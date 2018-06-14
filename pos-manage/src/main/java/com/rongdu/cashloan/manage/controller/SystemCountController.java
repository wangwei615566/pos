package com.rongdu.cashloan.manage.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rongdu.cashloan.cl.service.SystemCountService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.ServletUtils;

/**
 * 后台登陆，首页统计数据
 * @author caitt
 * @version 1.0
 * @date 2017年3月15日下午2:28:26
 * Copyright 杭州融都科技股份有限公司 现金贷  All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Scope("prototype")
@Controller
public class SystemCountController extends ManageBaseController {
	
	@Resource
	private SystemCountService systemCountService;

	/**
	 * 统计首页信息
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/count/homeInfo.htm")
	public void homeInfo(HttpServletResponse response) throws Exception {
		Map<String,Object> data = systemCountService.systemCount();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 每天放款量
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/count/fifteenDaysLoan.htm")
	public void fifteenDaysLoan(HttpServletResponse response) throws Exception {
		Map<String,Object> data = systemCountService.fifteenDaysLoan();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 应还款量与实还款量
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/count/fifteenDaysNeedAndRealRepay.htm")
	public void fifteenDaysNeedAndRealRepay(HttpServletResponse response) throws Exception {
		Map<String,Object> data = systemCountService.fifteenDaysNeedAndRealRepay();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 用户地域分布
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/count/userMonthLoanByArea.htm")
	public void userMonthLoanByArea(HttpServletResponse response) throws Exception {
		Map<String,Object> data = systemCountService.userMonthLoanByArea();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 还款方式
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/count/repaySource.htm")
	public void repaySource(HttpServletResponse response) throws Exception {
		Map<String,Object> data = systemCountService.repaySource();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
}
