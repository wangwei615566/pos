package com.rongdu.cashloan.manage.controller;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.RcDataStatistics;
import com.rongdu.cashloan.cl.model.BorrowRepayStatisticsModel;
import com.rongdu.cashloan.cl.service.RepayStatisticService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 报表管理的Controller
 *
 * @author leon
 * @date 2017年9月25日 17:13
 * Copyright 武汉成长无限网络  arc All Rights Reserved
 *
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Scope("prototype")
@Controller
public class ManageReportForms extends ManageBaseController{

	@Resource
	private RepayStatisticService repayStatistics;

	/**
	 * 应还统计列表
	 * @param searchParams
	 * @param currentPage
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/RepaymentStatistics/list.htm", method = { RequestMethod.POST })
	public void repayList(
			@RequestParam(value = "searchParams", required = false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<BorrowRepayStatisticsModel> page = repayStatistics.listAllModel(params, currentPage, pageSize);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response, result);
	}

	/**
	 * 风控数据统计（动态）列表
	 * @param searchParams
	 * @param currentPage
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/riskControlDataDynamicStatistics/list.htm", method = { RequestMethod.POST })
	public void dynamicStatistics(
			@RequestParam(value = "searchParams", required = false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<RcDataStatistics> page = repayStatistics.listRiskControlDataModel(params, currentPage, pageSize);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response, result);
	}

}
