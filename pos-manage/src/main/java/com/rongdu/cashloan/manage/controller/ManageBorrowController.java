package com.rongdu.cashloan.manage.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.czwx.arbitrament.entity.CaseInfo;
import com.czwx.arbitrament.mapper.CaseInfoMapper;
import com.czwx.arbitrament.service.ArbitramentService;
import com.czwx.arbitrament.service.BorrowDataCollectService;
import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.model.ArbitraOrderModel;
import com.rongdu.cashloan.cl.model.CountChannelNameModel;
import com.rongdu.cashloan.cl.model.ManageBorrowModel;
import com.rongdu.cashloan.cl.model.ManageBorrowProgressModel;
import com.rongdu.cashloan.cl.model.OverdueOrderMode;
import com.rongdu.cashloan.cl.service.BorrowProgressService;
import com.rongdu.cashloan.cl.service.BorrowRepayLogService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.FddContractRecordService;
import com.rongdu.cashloan.cl.service.LoanDataService;
import com.rongdu.cashloan.cl.service.PayLogService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.cashloan.system.permission.annotation.RequiresPermission;

import tool.util.BeanUtil;
import tool.util.DateUtil;
import tool.util.StringUtil;

 /**
 * 借款信息表Controller
 * 
 * @author jdd
 * @version 1.0.0 
 * @date 2017-02-23 16:26:19
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Controller
@Scope("prototype")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ManageBorrowController extends ManageBaseController {

	private static final Logger logger = Logger.getLogger(ManageBorrowController.class);
	
	@Resource
	private ClBorrowService clBorrowService;
	@Resource
	private BorrowProgressService borrowProgressService;
	@Resource
	private BorrowRepayLogService borrowRepayLogService;
	@Resource
	private PayLogService payLogService;
	@Resource
	private FddContractRecordService fddContractRecordService;
	@Resource
	private EhCacheCacheManager cacheManager;
	@Resource
	private LoanDataService loanDataService;
	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	@Resource
	private ArbitramentService arbitramentService;
	@Resource
	private BorrowDataCollectService borrowDataCollectService;

//	@Resource
//    private UserFadadaService userFadadaService;
	/**
	 *借款信息列表
	 * @param current
	 * @param current
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/list.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:list",name = "借款信息列表")
	public void list(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<ManageBorrowModel> page =clBorrowService.listModel(params,current,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}

	/**
	 *借款进度列表
	 * @param searchParams
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/progress/list.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:progress:list",name = "借款进度列表")
	public void progresslist(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<ManageBorrowProgressModel> page =borrowProgressService.listModel(params,currentPage,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 人工复审查询
	 * @param searchParams
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/reviewList.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:reviewList",name = "人工复审通过列表")
	public void reviewList(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List stateList;
		Page<ManageBorrowModel> page = null;
		if (params != null) {
			String state = StringUtil.isNull(params.get("state"));
			if (StringUtil.isNotBlank(state)) {
				if (StringUtil.equals(state, BorrowModel.STATE_PASS)) {
					page = clBorrowService.listReview(currentPage,pageSize);
				}else {
					stateList = Arrays.asList(state);
					params.put("stateList", stateList);
					params.put("state", "");
					page = clBorrowService.listModel(params,currentPage,pageSize);
				}
			}else {
				stateList = Arrays.asList(BorrowModel.STATE_NEED_REVIEW,
						BorrowModel.STATE_REFUSED,BorrowModel.STATE_PASS);
				params.put("stateList", stateList);
				params.put("state", "");
				page = clBorrowService.listModel(params,currentPage,pageSize);
			}
		}
		Long countNoAmount = clBorrowService.countNoAmount();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put("countNoAmount", countNoAmount);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	/**
	 * 借款审核列表     
	 * @param searchParams
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/borrowList.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:borrowList",name = "借款审核状态列表")
	public void borrowList(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List stateList;
		if (params != null) {
			String state = StringUtil.isNull(params.get("state"));
			if (null != state &&!StringUtil.isBlank(state)) {
				//待自动审核
				if(state.equals(BorrowModel.STATE_PRE)){//10
					stateList = Arrays.asList(BorrowModel.STATE_PRE);
					params.put("stateList", stateList);
					params.put("state", "");
				}
				//自动审核失败
				if(state.equals(BorrowModel.STATE_AUTO_REFUSED)){//21
					stateList = Arrays.asList(BorrowModel.STATE_AUTO_REFUSED);
					params.put("stateList", stateList);
					params.put("state", "");
				}
				//人工复审  
				if(state.equals(BorrowModel.STATE_NEED_REVIEW)){//22
					stateList = Arrays.asList(BorrowModel.STATE_NEED_REVIEW,
							BorrowModel.STATE_REFUSED,BorrowModel.STATE_PASS);
				    params.put("stateList", stateList);
					params.put("state", "");
				}
				//自动审核通过
				if(state.equals(BorrowModel.STATE_AUTO_PASS)){//20
					stateList = Arrays.asList(BorrowModel.STATE_AUTO_PASS);
				    params.put("stateList", stateList);
					params.put("state", "");
				}
			}
		}
		Page<ManageBorrowModel> page = clBorrowService.listModel(params,currentPage,pageSize);
		List<CountChannelNameModel> countChannelName = clBorrowService.countChannelName();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put("countChannelName", countChannelName);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 借款还款信息列表     
	 * @param current
	 * @param searchParams
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/borrowRepayList.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:borrowRepayList",name = "借款还款信息列表 ")
	public void borrowRepayList(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		List stateList;
		if (params != null) {
			//放款列表
			String type= StringUtil.isNull(params.get("type"));
			if("repay".equals(type)){
			    stateList = Arrays.asList(BorrowModel.STATE_BAD,BorrowModel.STATE_DELAY,BorrowModel.STATE_REPAY,BorrowModel.STATE_REMISSION_FINISH,BorrowModel.STATE_FINISH);
			    params.put("stateList", stateList);
			    params.put("sort", "loanSort");
				String state = StringUtil.isNull(params.get("state"));
				if (null != state &&!StringUtil.isBlank(state)) {
					params.put("state", state);
				}
			}
			String state = StringUtil.isNull(params.get("state"));
			if (null != state &&!StringUtil.isBlank(state)) {
				//还款列表
				if(state.equals(BorrowModel.STATE_FINISH)){//40
					stateList = Arrays.asList(BorrowModel.STATE_FINISH,
							BorrowModel.STATE_REMISSION_FINISH);
					params.put("stateList", stateList);
					params.put("state", "");
				}
				//逾期中列表  
				if(state.equals(BorrowModel.STATE_DELAY)){//50
					stateList = Arrays.asList(BorrowModel.STATE_DELAY);
					params.put("stateList", stateList);
					params.put("arbList", 1);//仲裁标志
					params.put("state", "");
				}
				//坏账列表  
				if(state.equals(BorrowModel.STATE_BAD)){//90
					stateList = Arrays.asList(BorrowModel.STATE_BAD);
					params.put("stateList", stateList);
					params.put("state", "");
				}
				
			}
		}
		Page<ManageBorrowModel> page = clBorrowService.listBorrowModel(params,current,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 重新支付
	 * @param borrowId
	 */
	@RequestMapping(value="/modules/manage/borrow/payAgain.htm",method={RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:payAgain",name = "借款还款信息详细页面    ")
	public void payAgain(@RequestParam(value="borrowId") long borrowId){
		Borrow borrow  = clBorrowService.getById(borrowId);
		boolean flag  = payLogService.judge(borrowId);
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (null != borrow && flag
				&& (BorrowModel.STATE_AUTO_PASS.equals(borrow.getState()) || 
					BorrowModel.STATE_PASS.equals(borrow.getState()) ||
					BorrowModel.STATE_REPAY_FAIL.equals(borrow.getState()))) {
			clBorrowService.borrowLoan(borrow, DateUtil.getNow());
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);

		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "此借款状态不允许再次支付");
		}
	
		ServletUtils.writeToResponse(response,result);
	}

	/**
	 * 借款还款信息详细页面      
	 * @param borrowId
	 * @param borrowId
	 * @param borrowId
	 *
	 */
	@RequestMapping(value="/modules/manage/borrow/borrowRepayContent.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:borrowRepayContent",name = "借款还款信息详细页面    ")
	public void borrowRepayContent(@RequestParam(value="borrowId") long borrowId){
		ManageBorrowModel model = clBorrowService.getModelByBorrowId(borrowId);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, model);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 查询借款记录 
	 * @param userId
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrow/listBorrowLog.htm")
	public void listBorrowLog(
			@RequestParam(value="userId") long userId,
			@RequestParam(value = "borrowId",required=false) Long borrowId,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		if (borrowId!=null) {
			userId = clBorrowService.findByPrimary(borrowId).getUserId();
		}
		Map<String,Object> params = new HashMap<>();
		params.put("userId", userId);
		Page<ManageBorrowModel> page = clBorrowService.listBorrowModel(params, current, pageSize);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("list", page.getResult());
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	
	/**
	 * 后台人工复审功能 旧
	 * @param borrowId
	 * @throws Exception
	 */
	@RequestMapping(value="/modules/manage/borrow/verifyBorrow.htm")
	public void verifyBorrow(@RequestParam(value = "borrowId") Long borrowId,
			@RequestParam(value = "state") String state,
			@RequestParam(value = "remark") String remark) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>(); 
		try{
		    int msg =clBorrowService.manualVerifyBorrow(borrowId, state, remark);
			if(msg==1){
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "复审成功");
			}else{
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "复审失败");
			}
		} catch (Exception e) {
			logger.error(e);
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, e.getMessage());
		}
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 后台人工复审功能 新
	 * @param borrowId
	 * @throws Exception
	 */
	@RequestMapping(value="/modules/manage/borrow/verifyBorrowNew.htm")
	public void verifyBorrowNew(@RequestParam(value = "borrowId") Long borrowId,
			@RequestParam(value = "state") String state,
			@RequestParam(value = "remark") String remark,
			@RequestParam(value = "isEditPhoto") String isEditPhoto) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>(); 
		try{
		    int msg =clBorrowService.manualVerifyBorrowNew(borrowId, state, remark,isEditPhoto);
			if(msg==1){
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "复审成功");
				
				if(BorrowModel.STATE_NEED_REVIEW.equals(state)){
					//资金申请
					String assetFundAvailable = Global.getValue("asset_fund_available");
					logger.debug("资产申请----------------------------borrowId: "+borrowId+", assetFundAvailable: "+assetFundAvailable);
					if ("1".equals(assetFundAvailable)) {
						final Long borrowIdFinal = borrowId;
						cachedThreadPool.execute(new Runnable() {
							public void run() {
								loanDataService.loanApplyFund(borrowIdFinal);
							}
						});
					}
					
					verifyBorrowLoan(borrowId, BorrowModel.STATE_PASS, remark);
				}else{
					ServletUtils.writeToResponse(response,result);
				}
				return;
			}else{
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "复审失败");
			}
		} catch (Exception e) {
			logger.error(e);
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, e.getMessage());
		}
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 放款审核功能
	 * @param borrowId
	 * @throws Exception
	 */
	@RequestMapping(value="/modules/manage/borrow/verifyBorrowLoan.htm")
	public void verifyBorrowLoan(@RequestParam(value = "borrowId") Long borrowId,
			@RequestParam(value = "state") String state,
			@RequestParam(value = "remark") String remark) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>(); 
		try{
			final String orderNo = borrowProgressService.findOrderNo(borrowId);

            
		    int msg =clBorrowService.manualVerifyBorrow(borrowId, state, remark);
			if(msg==1){
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "放款审核成功");

				new Thread(new Runnable(){
					public void run() {
						fddContractRecordService.completeContract(orderNo);
					}
				}).start();

			}else{
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "放款审核失败");
			}
		} catch (Exception e) {
			logger.error(e);
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, e.getMessage());
		}
		ServletUtils.writeToResponse(response,result);
	}

	/**
	 * 重新发起审核
	 * @param request
	 * @param response
	 * @param borrowId
	 */
	@RequestMapping(value = "/modules/manage/borrow/reVerifyBorrowData.htm",method=RequestMethod.POST)
	public void reVerifyBorrowData(@RequestParam(value="borrowId") String borrowId){
		long[] ids = StringUtil.toLongs(borrowId.split(","));
		Map<String,Object> result = new HashMap<String,Object>();
		Cache cache = cacheManager.getCache("re_verify_token");
		boolean flag = false;
		try{
			for (int i = 0; i < ids.length; i++) {
				if(cache.get(borrowId) == null){
					cache.put(borrowId, "1");
					clBorrowService.reVerifyBorrowDataNew(ids[i]);
					cache.evict(borrowId);
					flag = true;
				}else{
					break;
				}
			}
		}catch(Exception e){
			cache.evict(borrowId);
			throw e;
		}
		if(flag){
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "提交成功，请等待处理结果");
		}else{
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "操作频繁，请稍后再试");
		}
		ServletUtils.writeToResponse(response,result);
	} 
	
	/**
	 * 人工通过机审
	 * @param borrowId
	 * @param state
	 * @param remark
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/borrow/passVerifyBorrow.htm",method=RequestMethod.POST)
	public void passVerifyBorrow(@RequestParam(value = "borrowId") Long borrowId,
			@RequestParam(value = "state") String state) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>(); 
		try{
		    int msg =clBorrowService.passVerifyBorrow(borrowId, state);
			if(msg==1){
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "复审成功");
			}else{
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "复审失败");
			}
		} catch (Exception e) {
			logger.error(e);
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, e.getMessage());
		}
		ServletUtils.writeToResponse(response,result);
	}
	/**
	 * 逾期报表列表
	 * @param searchParams
	 * @param current
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/repay/overdue/orderList.htm",method=RequestMethod.POST)
	public void overdueOrderList(
			@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<OverdueOrderMode> page =clBorrowService.listOverdueOrder(params,current,pageSize);
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}

	 /**
	  * 提交仲裁案件
	  * @param  loanBillNos
	  */
	 @RequestMapping(value = "/modules/manage/borrow/submitArbitrate.htm",method=RequestMethod.POST)
	 public void submitArbitrateCase(@RequestParam(value = "loanBillNos" , required = true) String loanBillNos) throws Exception {
		 Map<String, Object> result = new HashMap<String, Object>();
		 CaseInfoMapper caseInfoMapper =(CaseInfoMapper) BeanUtil.getBean("caseInfoMapper");
		 CaseInfo caseInfo = caseInfoMapper.selectByLoanBillNo(loanBillNos);
		 if(caseInfo == null || (caseInfo != null && ("13".equals(caseInfo.getProcessStatus()) || "4".equals(caseInfo.getProcessStatus()) || StringUtils.isBlank(caseInfo.getProcessStatus())))) {

		     if(caseInfo != null && "10".equals(caseInfo.getState())&& (StringUtils.isBlank(caseInfo.getProcessStatus())|| "13".equals(caseInfo.getProcessStatus()) ||
                     "4".equals(caseInfo.getProcessStatus()))) {
                 result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
                 result.put(Constant.RESPONSE_CODE_MSG, "案件已经提交,请勿重复提交!");
             }else {
				 List<Borrow> borrowList = borrowDataCollectService.getBorrowList(loanBillNos);
				 //补录用户仲裁证据
				 borrowDataCollectService.getClaimsTransfer(borrowList);
				 borrowDataCollectService.getIdAddressAndNation(borrowList);
				 borrowDataCollectService.getPaymentVoucher(borrowList);
				 borrowDataCollectService.getReceipt(borrowList);
                 result = arbitramentService.caseSubmit(loanBillNos);
                 if ("200".equals(result.get("code").toString())) {
                     result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
                     result.put(Constant.RESPONSE_CODE_MSG, "案件提交中!");
                 }
             }
		 }else if(caseInfo != null && "0".equals(caseInfo.getProcessStatus())) {
			 result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			 result.put(Constant.RESPONSE_CODE_MSG, "案件提交成功,请勿重复提交!");
		 }else {
			 result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			 result.put(Constant.RESPONSE_CODE_MSG, "案件无法提交!");
		 }
		 ServletUtils.writeToResponse(response,result);
	 }

	 /**
	  * 提交仲裁案件
	  * @param  loanBillNos
	 * @throws IOException 
	  */
	 @RequestMapping(value = "/modules/manage/borrow/submitBatchArbitrate.htm",method=RequestMethod.POST)
	 public void submitBatchArbitrateCase(@RequestParam("file") MultipartFile file,HttpServletResponse response){
		 Map<String, Object> result = new HashMap<String, Object>();
		 if(!file.isEmpty()){
			InputStream is = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			try {
				is = file.getInputStream();
				isr = new InputStreamReader(is);
				br = new BufferedReader(isr);
				String orderNo = null;
				//从上传文件中读取订单号
				List<String> orderNos = new ArrayList<String>();
				while((orderNo = br.readLine()) != null){
					orderNos.add(orderNo.trim());
				}
				//查询案件订单表
				CaseInfoMapper caseInfoMapper =(CaseInfoMapper) BeanUtil.getBean("caseInfoMapper");
				List<CaseInfo> caseInfoList = caseInfoMapper.selectListByLoanBillNos(orderNos.toArray(new String[orderNos.size()]));
				int csize = caseInfoList.size();
				for(int i = 0; i < csize; i++){
					CaseInfo caseInfo = caseInfoList.get(i);
						//移除已提交案件订单号
					     if(caseInfo != null && "10".equals(caseInfo.getState())&& (StringUtils.isBlank(caseInfo.getProcessStatus())|| "13".equals(caseInfo.getProcessStatus()) ||
			                     "4".equals(caseInfo.getProcessStatus()))) {
					    	 orderNos.remove(caseInfo.getLoanBillNo());
			             }
				}
				StringBuilder sb = new StringBuilder();//存放可提交冲裁案件订单
				//拼接参数
				for(String param : orderNos){
					sb.append(param).append(",");
				}
				String subOrderNo = "".equals(sb.toString())  ? null : sb.substring(0, sb.length()-1);//可提交仲裁订单
				if(subOrderNo !=  null){
					List<Borrow> borrowList = borrowDataCollectService.getBorrowList(subOrderNo);
					 //补录用户仲裁证据
					 borrowDataCollectService.getClaimsTransfer(borrowList);
					 borrowDataCollectService.getIdAddressAndNation(borrowList);
					 borrowDataCollectService.getPaymentVoucher(borrowList);
					 borrowDataCollectService.getReceipt(borrowList);
		            result = arbitramentService.caseSubmit(subOrderNo);
		            if ("200".equals(result.get("code").toString())) {
		                result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		                result.put(Constant.RESPONSE_CODE_MSG, "案件提交中!");
		            }
				}
				else{
					result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
	                result.put(Constant.RESPONSE_CODE_MSG, "案件都已提交，请勿重复提交!");
				}
			} catch (IOException e) {
				logger.error("submitBatchArbitrateCase-系统异常",e);
				result.put(Constant.RESPONSE_CODE, Constant.OTHER_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "系统异常");
			}
			finally{
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						logger.error("submitBatchArbitrateCase-关闭输出流流异常",e);
					}
				}
			}
		 }
		 else{
			 result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			 result.put(Constant.RESPONSE_CODE_MSG, "请上传文件!");
		 }
		 ServletUtils.writeToResponse(response,result);
	 }

	 /**
	  * 案件立案申请
	  * @param  loanBillNos
	  * @param  batchNo
	  */
	 @RequestMapping(value = "/modules/manage/borrow/applyArbitrate.htm",method=RequestMethod.POST)
	 public void applyArbitrateCase(@RequestParam(value = "loanBillNos" , required = false) String loanBillNos,
								 @RequestParam(value = "batchNo", required = false) String batchNo) throws Exception {
		 Map<String, Object> result = new HashMap<String, Object>();
		 CaseInfoMapper caseInfoMapper =(CaseInfoMapper) BeanUtil.getBean("caseInfoMapper");
		 CaseInfo caseInfo = caseInfoMapper.selectByLoanBillNo(loanBillNos);
		 if (caseInfo != null && "0".equals(caseInfo.getProcessStatus())) {
		 	if("20".equals(caseInfo.getState())) {
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "立案已经受理,请勿重复立案!");
			} else {
				result = arbitramentService.caseApplication(loanBillNos,batchNo);
				if("200".equals(result.get("code").toString())) {
					result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
					result.put(Constant.RESPONSE_CODE_MSG, "立案受理中!");
					JSONObject jsonObject = JSONObject.parseObject(result.get("data").toString());
					JSONArray failedListObject = jsonObject.getJSONArray("failedList");
					for (int i = 0; i < failedListObject.size(); i++) {
						JSONObject failed = failedListObject.getJSONObject(i);
						if (loanBillNos.equals(failed.getString("loanBillNo"))) {
							result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
							result.put(Constant.RESPONSE_CODE_MSG, failed.getString("errMsg"));
						}
					}
				}
			}
		 }else if (caseInfo != null && "1".equals(caseInfo.getProcessStatus())) {
			 result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			 result.put(Constant.RESPONSE_CODE_MSG, "立案成功!");
		 }else if (caseInfo != null && "70".equals(caseInfo.getProcessStatus())) {
		 	 result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			 result.put(Constant.RESPONSE_CODE_MSG, "立案已经受理,请勿重复立案!");
		 } else {
		 	result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "此案件不能立案!");
	 	}
		 ServletUtils.writeToResponse(response,result);
	 }


	 /**
	  * 撤回仲裁
	  * @param loanBillNo
	  * @param type
	  * @param reason
	  */
	 @RequestMapping(value = "/modules/manage/borrow/revokerArbitrate.htm",method=RequestMethod.POST)
	 public void revokerArbitrateCase(@RequestParam(value = "loanBillNos", required = true) String loanBillNo,
									  @RequestParam(value = "type", required = true) String type,
									  @RequestParam(value = "reason", required = true) String reason) {
		 Map<String, Object> result = new HashMap<>();
		 CaseInfoMapper caseInfoMapper =(CaseInfoMapper) BeanUtil.getBean("caseInfoMapper");
		 CaseInfo caseInfo = caseInfoMapper.selectByLoanBillNo(loanBillNo);
		 if(caseInfo != null && ("40".equals(caseInfo.getState()) || "3".equals(caseInfo.getProcessStatus()))) {
			 result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			 result.put(Constant.RESPONSE_CODE_MSG, "案件撤销中,请勿重复撤销!");
		 }else if(caseInfo != null && "4".equals(caseInfo.getProcessStatus())) {
			 result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			 result.put(Constant.RESPONSE_CODE_MSG, "案件撤销成功!");
		 }else if(caseInfo != null && ("0".equals(caseInfo.getProcessStatus())||"1".equals(caseInfo.getProcessStatus()) || "5".equals(caseInfo.getProcessStatus()) || "6".equals(caseInfo.getProcessStatus()) ||
		 	"7".equals(caseInfo.getProcessStatus()) || "8".equals(caseInfo.getProcessStatus()))){
			 result = arbitramentService.caseCancel(loanBillNo, type, reason);
			 if ("200".equals(result.get("code").toString())) {
				 result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				 result.put(Constant.RESPONSE_CODE_MSG, "案件撤销中!");
			 }
		 }
		 ServletUtils.writeToResponse(response,result);
	 }


	 /**
	  * 查询仲裁案件详情
	  * @param orderNo
	  */
	 @RequestMapping(value = "/modules/manage/borrow/lookArbitrateDetail.htm",method=RequestMethod.POST)
	 public void lookArbitrateDetail(@RequestParam(value = "orderNo", required = true) String orderNo
									 ) {
		 Map<String,Object> data =  clBorrowService.lookArbitrateDetail(orderNo);
		 Map<String,Object> result = new HashMap<String,Object>();
		 result.put(Constant.RESPONSE_DATA, data);
		 result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		 result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		 ServletUtils.writeToResponse(response,result);
	 }

	 /**
	  * 仲裁订单列表
	  * @param searchParams
	  * @param current
	  * @param pageSize
	  */
	 @RequestMapping(value="/modules/manage/borrow/repay/arbitra/list.htm",method=RequestMethod.POST)
	 public void arbitraOrderList(
			 @RequestParam(value="searchParams",required=false) String searchParams,
			 @RequestParam(value = "current") int current,
			 @RequestParam(value = "pageSize") int pageSize){
		 Map<String,Object> result = new HashMap<String,Object>();
		 Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		 Page<ArbitraOrderModel> page =clBorrowService.listArbitraOrder(params,current,pageSize);
		 result.put(Constant.RESPONSE_DATA, page);
		 result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		 result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		 result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		 ServletUtils.writeToResponse(response,result);
	 }

	 /**
	  * 仲裁金额查询

	  */
	 @RequestMapping(value="/modules/manage/borrow/repay/arbitra/queryBalance.htm")
	 public void arbitraBalance(){
		 Map<String,Object> data= arbitramentService.queryBalance();
		 String dataStr = data.get(Constant.RESPONSE_DATA).toString();
		 Map<String,Object> rmap=JsonUtil.parse(dataStr,Map.class);
		 Integer blances = Integer.parseInt(rmap.get("blances").toString())/100;
		 rmap.put("blances", blances);
		 data.put(Constant.RESPONSE_DATA,rmap);
		 ServletUtils.writeToResponse(response,data);
	 }

}
