package com.rongdu.cashloan.manage.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rongdu.cashloan.cl.domain.RcDataStatistics;
import com.rongdu.cashloan.cl.service.BorrowRepayLogService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.OperatorReqLogService;
import com.rongdu.cashloan.cl.service.PayCheckService;
import com.rongdu.cashloan.cl.service.PayLogService;
import com.rongdu.cashloan.cl.service.RcDataStatisticsService;
import com.rongdu.cashloan.cl.service.RepayStatisticService;
import com.rongdu.cashloan.cl.service.UrgeRepayOrderService;
import com.rongdu.cashloan.core.common.context.ExportConstant;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.excel.JsGridReportBase;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.cashloan.core.service.CloanUserService;
import com.rongdu.cashloan.system.domain.SysUser;

import tool.util.StringUtil;

@Scope("prototype")
@Controller
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ManageListExport extends ManageBaseController{

	@Resource
	private ClBorrowService clBorrowService;
	@Resource
	private CloanUserService userService;
	@Resource
	private OperatorReqLogService operatorReqLogService;
	@Resource
	private BorrowRepayLogService borrowRepayLogService;
	@Resource
	private PayLogService payLogService;
	@Resource
	private PayCheckService payCheckService;
	@Resource
	private UrgeRepayOrderService urgeRepayOrderService;
	@Resource
	private RepayStatisticService repayStatisticService;
	@Resource
	private RcDataStatisticsService rcDataStatisticsService;

	/**
	 * 导出还款记录报表
	 * 
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrowRepayLog/export.htm")
	public void repayLogExport(
			@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = borrowRepayLogService.listExport(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "还款记录Excel表";
		String[] hearders =  ExportConstant.EXPORT_REPAYLOG_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_REPAYLOG_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出借款订单报表
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrow/export.htm")
	public void borrowExport(
			@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = clBorrowService.listBorrowExport(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "借款订单Excel表";
		String[] hearders =  ExportConstant.EXPORT_BORROW_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_BORROW_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出支付记录报表
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/payLog/export.htm")
	public void payLogExport(
			@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		List list = payLogService.listPayLog(searchParams);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "支付记录Excel表";
		String[] hearders =  ExportConstant.EXPORT_PAYLOG_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_PAYLOG_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出支付对账记录报表
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/payCheck/export.htm")
	public void payCheckExport(
			@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = payCheckService.listPayCheck(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "支付对账记录Excel表";
		String[] hearders =  ExportConstant.EXPORT_PAYCHECK_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_PAYCHECK_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出已逾期订单报表
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/overdue/export.htm")
	public void overdueExport(
			@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		Map<String, Object> params;
		if (StringUtil.isBlank(searchParams)) {
			params = new HashMap<>();
		}else {
			searchParams = URLDecoder.decode(searchParams,"utf-8");
			params = JsonUtil.parse(searchParams, Map.class);
		}
		params.put("state", BorrowModel.STATE_DELAY);
		List list = clBorrowService.listBorrow(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "已逾期订单Excel表";
		String[] hearders =  ExportConstant.EXPORT_OVERDUE_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_OVERDUE_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出已坏账订单报表
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/badDebt/export.htm")
	public void badDebtExport(
			@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		Map<String, Object> params;
		if (StringUtil.isBlank(searchParams)) {
			params = new HashMap<>();
		}else {
			searchParams = URLDecoder.decode(searchParams,"utf-8");
			params = JsonUtil.parse(searchParams, Map.class);
		}
		params.put("state", BorrowModel.STATE_BAD);
		List list = clBorrowService.listBorrow(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "已坏账订单Excel表";
		String[] hearders =  ExportConstant.EXPORT_BADDEBT_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_BADDEBT_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出催收订单报表
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/urgeRepayOrder/export.htm")
	public void urgeRepayOrderExport(
			@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = urgeRepayOrderService.listUrgeRepayOrder(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "催收订单Excel表";
		String[] hearders =  ExportConstant.EXPORT_REPAYORDER_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_REPAYORDER_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出催收反馈报表
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/urgeLog/export.htm")
	public void urgeLogExport(
			@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = urgeRepayOrderService.listUrgeLog(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "催收反馈Excel表";
		String[] hearders =  ExportConstant.EXPORT_URGELOG_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_URGELOG_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出还款结算记录报表
	 * 
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrowRepaySettle/export.htm")
	public void repaySettleExport(@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = borrowRepayLogService.listSettleExport(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "还款结算记录Excel表";
		String[] hearders =  ExportConstant.EXPORT_REPAYLOG_SETTLE_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_REPAYLOG__SETTLE_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	/**
	 * 导出催回率统计报表
	 * 
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/urge/collection/urgeCount/export.htm")
	public void urgeCountExport(@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = urgeRepayOrderService.urgeCountExport(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "催回率统计Excel表";
		String[] hearders =  ExportConstant.EXPORT_COLLECTION_SUCCESS_COUNT_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_COLLECTION_SUCCESS_COUNT_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	/**
	 * 导出逾期大于15天报表
	 * 
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/overdue/orderList/export.htm")
	public void overdueOrderExport(@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = clBorrowService.overdeOrderExport(params);
		if (CollectionUtils.isEmpty(list)) {
			list=new ArrayList<>();
		}
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "逾期15天以上Excel表";
		String[] hearders =  ExportConstant.EXPORT_COLLECTION_OVERDUE_ORDER_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_COLLECTION_OVERDUE_ORDER_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	/**
	 * 导出应还统计报表
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/repayStatistics/export.htm")
	public void repayStatisticsExport(@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = repayStatisticService.listRepayStatisticsExport(params);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "应还统计记录Excel表";
		String[] hearders =  ExportConstant.EXPORT_REPAYMENTSTATISTICS_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_REPAYMENTSTATISTICS_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}

	/**
	 * 导出风控数据统计（动态）报表
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/riskControlDataDynamicStatistics/export.htm")
	public void riskControlDataExport(@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = repayStatisticService.listRiskControlDataExport(params);
		List listSumExcel = repayStatisticService.listSumExcelExport(params);
		list.addAll(listSumExcel);
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "风控数据动态统计Excel报表";
		String[] hearders =  ExportConstant.EXPORT_RISKCONTROLDYNAMICDATA_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_RISKCONTROLDYNAMICDATA_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
	
	/**
	 * 导出风控数据统计报表
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/rcDataStatistics/export.htm")
	public void rcDataStatisticsExport(@RequestParam(value="searchParams",required = false) String searchParams) throws Exception {
		searchParams = URLDecoder.decode(searchParams,"utf-8");
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List list = rcDataStatisticsService.listExport(params);
		if(!CollectionUtils.isEmpty(list)){
			RcDataStatistics listAllSum = rcDataStatisticsService.listAllSum(params);
			list.add(listAllSum);
		}
		SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
		response.setContentType("application/msexcel;charset=UTF-8");
		// 记录取得
		String title = "风控数据统计Excel表";
		String[] hearders =  ExportConstant.EXPORT_RCDATA_STATISTICS_LIST_HEARDERS;
		String[] fields = ExportConstant.EXPORT_RCDATA__STATISTICS_LIST_FIELDS;
		JsGridReportBase report = new JsGridReportBase(request, response);
		report.exportExcel(list,title,hearders,fields,user.getName());
	}
}
