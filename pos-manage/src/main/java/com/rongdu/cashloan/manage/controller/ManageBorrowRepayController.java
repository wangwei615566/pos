package com.rongdu.cashloan.manage.controller;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.BorrowDetail;
import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.model.BorrowRepayModel;
import com.rongdu.cashloan.cl.model.ManageBRepayModel;
import com.rongdu.cashloan.cl.model.ManageBorrowModel;
import com.rongdu.cashloan.cl.model.RepayExcelModel;
import com.rongdu.cashloan.cl.service.BorrowDetailService;
import com.rongdu.cashloan.cl.service.BorrowRepayLogService;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.ClSmsService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.exception.BussinessException;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.cashloan.system.domain.SysUser;
import com.rongdu.cashloan.system.permission.annotation.RequiresPermission;
import com.rongdu.cashloan.system.service.SysUserRoleService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import tool.util.BigDecimalUtil;
import tool.util.DateUtil;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 还款计划表和还款记录Controller
 * 
 * @author jdd
 * @version 1.0.0
 * @date 2017-02-24 14:02:41 
 * Copyright 杭州融都科技股份有限公司 arc All Rights Reserved
 * 官方网站：www.erongdu.com 
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Controller
@Scope("prototype")
@SuppressWarnings({ "unchecked" })
public class ManageBorrowRepayController extends ManageBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(ManageBorrowRepayController.class);
	@Resource
	private BorrowRepayService borrowRepayService;
	@Resource
	private BorrowRepayLogService borrowRepayLogService;
	@Resource
	private SysUserRoleService sysUserRoleService;
	@Resource
	private ClSmsService clSmsService;
	@Resource
	private BorrowDetailService borrowDetailService;
	@Resource
	private ClBorrowService clBorrowService;

	/**
	 * 还款计划列表
	 * 
	 * @param searchParams
	 * @param currentPage
	 * @param pageSize
	 * @throws ServiceException 
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/list.htm")
	@RequiresPermission(code = "modules:manage:borrow:repay:list", name = "还款信息列表")
	public void list(
			@RequestParam(value = "searchParams", required = false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize) throws ServiceException {
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<ManageBRepayModel> page = borrowRepayService.listModel(params,
				currentPage, pageSize);
		SysUser sysUser = (SysUser)request.getSession().getAttribute("SysUser");
		List<String> nids = sysUserRoleService.roleNameMap(sysUser);
		String roleYes = "0";
		if (nids.contains("system")||nids.contains("financialStaff")) {
			roleYes="1";
		}
		List<Map<String, Integer>> repayCount = borrowRepayService.repayCount(params);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put("roleYes", roleYes);
		result.put("repayCount", repayCount);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response, result);
	}

	/**
	 * 确认还款
	 * @param id  还款计划id
	 * @param amount  还款金额
	 * @param penaltyAmout   逾期罚息
	 * @param repayTime  还款时间
	 * @param repayWay   还款方式
	 * @param serialNumber 流水号
	 * @param repayAccount 还款账号
	 * @param state 正常还款  10  ，金额减免 20
	 * @param vcode 短信验证码
	 * @throws
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/confirmRepay.htm", method = {RequestMethod.POST })
	@RequiresPermission(code = "modules:manage:borrow:repay:confirmRepay", name = "确认还款")
	public void confirmRepay(
			@RequestParam(value = "id") Long id,
			@RequestParam(value = "amount", required = false) String amount,
			@RequestParam(value = "penaltyAmout", required = false) String penaltyAmout,
			@RequestParam(value = "repayTime") String repayTime,
			@RequestParam(value = "repayWay") String repayWay,
			@RequestParam(value = "serialNumber") String serialNumber,
			@RequestParam(value = "repayAccount") String repayAccount,
			@RequestParam(value = "state") String state,
			@RequestParam(value = "vcode") String vcode,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "deratereAmount", required=false) String deratereAmount,
			@RequestParam(value = "derateRemark", required=false) String derateRemark,
			@RequestParam(value = "derateDay", required=false) Integer derateDay
			) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		int msg = clSmsService.verifySms(phone, "confirmRepayCode", vcode);
		if (msg == 1) {
			try{
				BorrowRepay br = borrowRepayService.getById(id);
				Map<String, Object> param = new HashMap<String, Object>();
				if (derateDay!=null && derateDay!=0) {
					Borrow borrow = clBorrowService.findByPrimary(br.getBorrowId());
					Double penaltyAmout1 = Double.parseDouble(penaltyAmout);
					BorrowDetail borrowDetail = borrowDetailService.findByBorrowId(br.getBorrowId());
					Double amout = BigDecimalUtil.decimal(BigDecimalUtil.mul(borrow.getAmount(), borrowDetail.getOverduePenaltyRatio()),2)*derateDay;
					penaltyAmout = ""+(penaltyAmout1 -amout);
					param.put("derateDay", derateDay);
				}
				SysUser sysUser = (SysUser)request.getSession().getAttribute("SysUser");
				String accessCode = (String) request.getSession().getAttribute(Constant.ACCESSCODE);
				param.put("id", id);
				param.put("repayTime",DateUtil.valueOf(repayTime, DateUtil.DATEFORMAT_STR_001));
				param.put("repayWay", repayWay);
				param.put("repayAccount", repayAccount);
				param.put("serialNumber", serialNumber);
				param.put("penaltyAmout", penaltyAmout);
				param.put("state", state);
				param.put("operator", sysUser.getUserName());
				param.put("accessCode", accessCode);
				if (StringUtil.isNotBlank(deratereAmount)) {
					param.put("deratereAmount", deratereAmount);
					if (StringUtil.isNotBlank(derateRemark)) {
						param.put("derateRemark", derateRemark);
					}
				}
				if (br != null) {
					param.put("amount", br.getAmount());
					if (!br.getState().equals(BorrowRepayModel.STATE_REPAY_YES)) {
						resultMap = borrowRepayService.confirmRepay(param);
					} else {
						resultMap.put("Code", Constant.FAIL_CODE_VALUE);
						resultMap.put("Msg", "该还款计划已还款");
					}
				} else {
					resultMap.put("Code", Constant.FAIL_CODE_VALUE);
					resultMap.put("Msg", "还款计划不存在");
				}
			} catch (Exception e) {
				logger.info(e.getMessage(),e);
				resultMap.put("Code", Constant.FAIL_CODE_VALUE);
				resultMap.put("Msg", "还款失败");
			}
		} else if (msg == -1) {
			data.put("message", "验证码已过期");
			data.put("state", "20");
			resultMap.put(Constant.RESPONSE_DATA, data);
			resultMap.put("Code", Constant.FAIL_CODE_VALUE);
			resultMap.put("Msg", "验证码已过期");
		} else {
			data.put("message", "手机号码或验证码错误");
			data.put("state", "20");
			resultMap.put(Constant.RESPONSE_DATA, data);
			resultMap.put("Code", Constant.FAIL_CODE_VALUE);
			resultMap.put("Msg", "手机号码或验证码错误");
		}
		result.put(Constant.RESPONSE_CODE, resultMap.get("Code"));
		result.put(Constant.RESPONSE_CODE_MSG, resultMap.get("Msg"));
		ServletUtils.writeToResponse(response, result);
	}

	/**
	 * 后台催收管理列表
	 * 
	 * @param searchParams
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value = "/modules/manage/borrow/repayList.htm", method = {RequestMethod.POST, RequestMethod.GET })
	@RequiresPermission(code = "modules:manage:borrow:repayList.htm", name = "催收管理列表")
	public void repayList(
			@RequestParam(value = "searchParams", required = false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize) {
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		if (params == null) {
			params = new HashMap<String, Object>();
			List<String> stateList = Arrays.asList(BorrowModel.STATE_DELAY,
					BorrowModel.STATE_BAD, BorrowModel.STATE_REPAY); // 未收款的借款
			params.put("stateList", stateList);
			params.put("state", BorrowModel.STATE_REPAY);
		} else {
			String state = StringUtil.isNull(params.get("state"));
			if (null == state || StringUtil.isBlank(state)) {
				List<String> stateList = Arrays.asList(BorrowModel.STATE_DELAY,
						BorrowModel.STATE_BAD, BorrowModel.STATE_REPAY); // 未收款的借款
				params.put("stateList", stateList);
				params.put("state", BorrowModel.STATE_REPAY);
			} else {
				List<String> stateList = Arrays.asList(state); // 未收款的借款
				params.put("stateList", stateList);
				params.put("state", state);
			}
		}
		Page<ManageBorrowModel> page = borrowRepayService.listRepayModel(
				params, currentPage, pageSize);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response, result);
	}
	/**
	 * 文件上传批量还款
	 * @param repayFile
	 * @param type
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/fileBatchRepay.htm", method = {RequestMethod.POST })
	@RequiresPermission(code = "modules:manage:borrow:repay:fileBatchRepay", name = "文件上传批量还款")
	public void fileBatchRepay(
			@RequestParam(value = "repayFile") MultipartFile repayFile,
			@RequestParam(value = "type") String type) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<List<String>> list=new ArrayList<List<String>>();
	    try {
	    	list=borrowRepayService.fileBatchRepay(repayFile,type);
	    	String title = "批量还款匹配结果";
	    	RepayExcelModel report = new RepayExcelModel();
	    	String fileName=report.saveExcelByList(list, title, repayFile.getOriginalFilename(),request);
	    	result.put(Constant.RESPONSE_DATA, "/modules/manage/borrow/repay/downRepayByFile.htm?path="+fileName);
	    	result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		} catch (BussinessException e) {
			logger.error(e.getMessage(),e);
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "批量还款失败");
		}
	    ServletUtils.writeToResponse(response, result);
	    
	}
	/**
	 * 指定文件下载
	 * @param path
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/downRepayByFile.htm", method = {RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:downRepayByFile", name = "指定文件下载")
	public void fileBatchRepay(
			@RequestParam(value = "path") String path) throws IOException {
		RepayExcelModel report = new RepayExcelModel();
		report.exportFile(path,request,response);
		try {
			report.exportFile(path,request,response);
		} catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 代扣还款
	 * @param repayId
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/doRepay.htm", method = {RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:doRepay", name = "代扣还款")
	public void doRepay(
			@RequestParam(value = "repayId") long repayId) throws IOException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,String> result = borrowRepayService.repay(repayId, ServletUtils.getIpAddress(request));
		if("0".equals(result.get("code"))){
			resultMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		}else{
			resultMap.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
		}
		resultMap.put(Constant.RESPONSE_CODE_MSG, result.get("msg"));
		ServletUtils.writeToResponse(response, resultMap);
	}
	/**
	 * 减免还款
	 * @param repayId
	 * @param derateAmount
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/doDerateRepay.htm", method = {RequestMethod.POST})
	public void doDerateRepay(
			@RequestParam(value = "repayId") long repayId,@RequestParam(value = "derateAmount") Double derateAmount,@RequestParam(value = "derateRemark")String derateRemark) throws IOException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,String> result = borrowRepayService.deraterepay(repayId, ServletUtils.getIpAddress(request),derateAmount,derateRemark);
		if("0".equals(result.get("code"))){
			resultMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		}else{
			resultMap.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
		}
		resultMap.put(Constant.RESPONSE_CODE_MSG, result.get("msg"));
		ServletUtils.writeToResponse(response, resultMap);
	}
	/**
	 * 获取短信验证码(确认还款)
	 * @param
	 * @param
	 */
	@RequestMapping(value = "/modules/manage/borrow/repay/getCode.htm", method = {RequestMethod.POST})
	public void getCode(
			@RequestParam(value = "phone") String phone) throws IOException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		long countDown = clSmsService.findTimeDifference(phone, "confirmRepayCode");
		if(StringUtils.isBlank(phone) || ! com.rongdu.cashloan.core.common.util.StringUtil.isPhone(phone)) {
			data.put("message", "手机号不正确");
			resultMap.put(Constant.RESPONSE_DATA, data);
			resultMap.put("Code",Constant.FAIL_CODE_VALUE);
			resultMap.put("Msg", "手机号不正确");
		}else if (countDown != 0) {
			data.put("countDown", countDown);
			String message = "获取短信验证码过于频繁，请稍后再试";
			resultMap.put("Code",Constant.FAIL_CODE_VALUE);
			resultMap.put("Msg", message);
		} else {
			long msg = clSmsService.sendSms("confirmRepayCode", data, phone);
			if (msg == 1) {
				data.put("message", "短信发送成功");
				resultMap.put(Constant.RESPONSE_DATA, data);
				resultMap.put("Code",Constant.SUCCEED_CODE_VALUE);
				resultMap.put("Msg", "短信发送成功");
			} else {
				data.put("message", "短信发送失败");
				resultMap.put(Constant.RESPONSE_DATA, data);
				resultMap.put("Code",Constant.FAIL_CODE_VALUE);
				resultMap.put("Msg", "短信发送失败");
			}
		}
		result.put(Constant.RESPONSE_CODE, resultMap.get("Code"));
		result.put(Constant.RESPONSE_CODE_MSG, resultMap.get("Msg"));
		ServletUtils.writeToResponse(response, result);
	}
}
