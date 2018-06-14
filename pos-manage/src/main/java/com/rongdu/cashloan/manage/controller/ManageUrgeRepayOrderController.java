package com.rongdu.cashloan.manage.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rongdu.cashloan.cl.domain.UrgeMemberModifyLog;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.domain.UrgeRepayOrder;
import com.rongdu.cashloan.cl.domain.UrgeRepayOrderLog;
import com.rongdu.cashloan.cl.model.BorrowRepayModel;
import com.rongdu.cashloan.cl.model.ManageBorrowModel;
import com.rongdu.cashloan.cl.model.UrgeRepayOrderModel;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.UrgeRepayOrderLogService;
import com.rongdu.cashloan.cl.service.UrgeRepayOrderService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.cashloan.system.domain.SysUser;
import com.rongdu.cashloan.system.permission.annotation.RequiresPermission;
import com.rongdu.cashloan.system.service.SysDictService;
import com.rongdu.cashloan.system.service.SysUserRoleService;

import tool.util.DateUtil;

 /**
 * 催款计划记录表Controller
 * 
 * @author jdd
 * @version 1.0.0
 * @date 2017-03-07 14:21:58
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Scope("prototype")
@Controller
public class ManageUrgeRepayOrderController extends ManageBaseController {

	private static final Logger logger = Logger.getLogger(ManageUrgeRepayOrderController.class);
	
	@Resource
	private UrgeRepayOrderService urgeRepayOrderService;
	
	@Resource
	private UrgeRepayOrderLogService urgeRepayOrderLogService;

	@Resource
	private BorrowRepayService borrowRepayService;
	
	@Resource
	private SysDictService sysDictService;
	
	@Resource
	private SysUserRoleService sysUserRoleService;
	
	/**
	 *催款计划信息列表
	 * @param search
	 * @param currentPage
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/borrow/repay/urge/list.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:list",name = "催款计划信息列表")
	public void list(HttpServletRequest request,@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception{
		try{
			searchParams = URLDecoder.decode(searchParams,"utf-8");
		} catch(Exception e){
			
		}
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		if(params==null){
			params=new HashMap<String, Object>();
		}
		//权限控制，不同的角色能看到的催收总订单不同
 		/*SysUser sysUser=getLoginUser(request);
		if(sysUser!=null){
			if(params==null){
				params=new HashMap<String, Object>();
			}
			if(sysUser.getId()!=1){
				params.put("userId",sysUser.getId());	
			}
			List<SysUserRole> roleList = sysUserRoleService.getSysUserRoleList(sysUser.getId());
			if(roleList == null){
				params.put("userId",sysUser.getId());
			}
			else{
				int size = roleList.size();
				for(int i = 0; i < size; i++){
					SysUserRole syr = roleList.get(i);
					SysRole sr = sysUserService.getRoleById(syr.getRoleId());
					if("system".equals(sr.getNid()) || "collector".equals(sr.getNid())){
						break;
					}
					else{
						params.put("userId",sysUser.getId());
					}
				}
			}
		} */
		Page<UrgeRepayOrder> page =urgeRepayOrderService.list(params,current,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 *催款记录信息列表
	 * @param search
	 * @param currentPage
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/borrow/repay/urge/loglist.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:loglist",name = "催款记录信息列表")
	public void loglist(HttpServletRequest request, @RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
	 	SysUser sysUser=getLoginUser(request);
		if(sysUser!=null){
			if(params==null){
				params=new HashMap<String, Object>();
			}
			if(sysUser.getId()!=1){
				params.put("userId",sysUser.getId());	
			}
		} 
		Page<UrgeRepayOrderModel> page =urgeRepayOrderService.listModel(params,current,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 *催款专员信息列表
	 * @param search
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/repay/urge/sysUserlist.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:sysUserlist",name = "催款专员信息列表")
	public void sysUserlist(@RequestParam(value = "roleName",required=false) String roleName)throws Exception {
			Map<String, Object> responseMap = new HashMap<String, Object>();
			Map<String, Object> params = new HashMap<String, Object>();
			List<String> roleList = new ArrayList<String>();
			if(null==roleName){
				roleList.add("CollectionSpecialist");
				roleList.add("lctc");
				//params.put("roleName", "CollectionSpecialist");
			}else{
				//params.put("roleName", roleName);
				roleList.add(roleName);
			}
			params.put("roleName", roleList);
			//后续可能需要优化 根据当前登录者来筛选数据
			/*String officeOver = getLoginUser(request).getOfficeOver();
			params.put("officeOver", Arrays.asList(officeOver.split(",")));
			*/
			List<Map<String,Object>> users = sysUserService.getUserInfo(params);
			responseMap.put("data", users);
			responseMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			responseMap.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
			ServletUtils.writeToResponse(response, responseMap);
	}
	
	/**
	 *催款订单分配人员
	 * @param response
	 * @param request
	 * @param session
	 * @param ids
	 * @param userId
	 * @param userName
	 */
	@RequestMapping(value="/modules/manage/borrow/repay/urge/allotUser.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:allotUser",name = "催款订单分配人员")
	public void allotUser(
			@RequestParam(value = "id") String ids,
			@RequestParam(value = "userId") Long userId,
			@RequestParam(value = "userName") String userName)throws Exception {
			Map<String, Object> responseMap = new HashMap<String, Object>();
			Map<String, Object> params = new HashMap<String, Object>();
			UrgeMemberModifyLog urgeMemberModifyLog = new UrgeMemberModifyLog();
			SysUser sysUser=getLoginUser(request);
			if (sysUser!=null) {
				urgeMemberModifyLog.setAssignerId(sysUser.getId());
				urgeMemberModifyLog.setAssignerName(sysUser.getName());
			}
			urgeMemberModifyLog.setFirstAssign(Long.parseLong("1"));
			urgeMemberModifyLog.setUrgeId(userId);
			urgeMemberModifyLog.setUrgeName(userName);

			params.put("urgeMemberModifyLog",urgeMemberModifyLog);
			params.put("id", ids);
			params.put("userId", userId);
			params.put("userName", userName);
			params.put("updateTime", new Date());
			params.put("state", UrgeRepayOrderModel.STATE_ORDER_URGE);
			List<Integer> i = urgeRepayOrderService.orderAllotUser(params);
			
			if(Collections.min(i) > 0){
				responseMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				responseMap.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
			}else{
				responseMap.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				responseMap.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_FAIL);
			}
			ServletUtils.writeToResponse(response, responseMap);
	}
 
	/**
	 *催款订单详细记录
	 * @param search
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/repay/urge/listDetail.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:listDetail",name = "催款订单详细记录")
	public void listDetail(@RequestParam(value = "id") Long id) {
			Map<String, Object> data = new HashMap<String, Object>();
		    UrgeRepayOrder  order =urgeRepayOrderService.getById(id);
		    Map<String, Object> params = new HashMap<String, Object>();
		    params.put("dueId", id);
		    List<UrgeRepayOrderLog>  logs= urgeRepayOrderLogService.getLogByParam(params);
		    data.put("order", order);
		    data.put("logs", logs);
			Map<String,Object> result = new HashMap<String,Object>();
			result.put(Constant.RESPONSE_DATA, data);
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			
			result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
			ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 逾期借款未入催
	 * @param search
	 * @param currentPage 
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/borrow/repay/urge/borrowlist.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:borrowlist",name = "催款专员信息列表")
	public void borrowlist(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize)throws Exception {
			Map<String, Object> responseMap = new HashMap<String, Object>();
			Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
			if(params==null){
				   params = new HashMap<String, Object>();
			}
			params.put("state", BorrowModel.STATE_DELAY);
			List<UrgeRepayOrder> list =urgeRepayOrderService.listAll(new HashMap<String, Object>());
			List<Long> idList = new ArrayList<Long>();
			if (list != null && list.size() > 0) {
				params.put("type", "urge");
				for (UrgeRepayOrder or : list) {
					idList.add(or.getBorrowId());
				}
				params.put("idList", idList);
			} else {
				params.put("type", "");
			}
	
			Page<ManageBorrowModel> page =borrowRepayService.listModelNotUrge(params,currentPage,pageSize);
			responseMap.put(Constant.RESPONSE_DATA, page);
			responseMap.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
			responseMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			responseMap.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
			ServletUtils.writeToResponse(response, responseMap);
	}
	
	/**
	 * 新增催收订单信息
	 * @param search
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/repay/urge/addOrder.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:addOrder",name = "新增催收订单信息")
	public void addOrder(@RequestParam(value = "id") Long id) {
		 Map<String,Object> result = new HashMap<String,Object>();
		 Map<String,Object> resultmap=urgeRepayOrderService.saveOrder(id);
		 result.put(Constant.RESPONSE_CODE, resultmap.get("code"));
		 result.put(Constant.RESPONSE_CODE_MSG, resultmap.get("msg"));
		ServletUtils.writeToResponse(response,result);
	}
	/**
	 * 修改催收订单信息状态
	 * @param search
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/repay/urge/updateOrderState.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:updateOrderState",name = "修改催收订单信息状态")
	public void addOrder(@RequestParam(value = "id") Long id,@RequestParam(value = "state") String state) {
		 Map<String,Object> result = new HashMap<String,Object>();
		 Map<String,Object> param = new HashMap<String,Object>();
		 param.put("id", id);
		 param.put("state", state);
		 urgeRepayOrderService.updateLate(param);
		 result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		 result.put(Constant.RESPONSE_CODE_MSG, "提交成功");
		ServletUtils.writeToResponse(response,result);
	}
	/**
	 * 添加催款反馈信息
	 * @param search
	 * @param currentPage
	 * @param pageSize
	 */
	@RequestMapping(value="/modules/manage/borrow/repay/urge/saveOrderInfo.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:borrow:repay:urge:saveOrderInfo",name = "添加催款反馈信息")
	public void saveOrderInfo(@RequestParam(value = "dueId") Long dueId,
			@RequestParam(value = "createTime") String createTime,
			@RequestParam(value = "state") String state,
			@RequestParam(value = "remark") String remark,
			@RequestParam(value = "promiseTime",required=false) String promiseTime,
			@RequestParam(value = "way") String way) {
		 Map<String,Object> result = new HashMap<String,Object>();
		 UrgeRepayOrder order=urgeRepayOrderService.getById(dueId);
		 UrgeRepayOrderLog  orderLog=new UrgeRepayOrderLog();
		 if(order!=null){
			 try {
				 orderLog.setCreateTime(DateUtil.valueOf(createTime, DateUtil.DATEFORMAT_STR_001));
				 if(promiseTime!=null&&promiseTime!=""){
		           orderLog.setPromiseTime(DateUtil.valueOf(promiseTime, DateUtil.DATEFORMAT_STR_001));
				 }
				 orderLog.setRemark(remark);
				 orderLog.setWay(way);
				 if(state==null||state==""){
					 orderLog.setState(UrgeRepayOrderModel.STATE_ORDER_URGE);
				 }else{
				     orderLog.setState(state);
				 }
				 
				 Map<String, Object> paramMap = new HashMap<String, Object>(1);
				 paramMap.put("borrowId", order.getBorrowId());
				 BorrowRepay borrowRepay = borrowRepayService.findSelective(paramMap);
				 if (BorrowRepayModel.STATE_REPAY_YES.equals(borrowRepay.getState())) {
					 result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
					 result.put(Constant.RESPONSE_CODE_MSG, "客户已还款");
				 } else {
					 urgeRepayOrderLogService.saveOrderInfo(orderLog,order);
					 
					 result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
					 result.put(Constant.RESPONSE_CODE_MSG, "提交成功");
				 }
			 } catch (Exception e) {
				logger.error(e);
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "提交失败");
				}
			 
		 }else{
			 result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			 result.put(Constant.RESPONSE_CODE_MSG, "提交信息有误");
		 }
		 ServletUtils.writeToResponse(response,result);
	}
	
	

}
