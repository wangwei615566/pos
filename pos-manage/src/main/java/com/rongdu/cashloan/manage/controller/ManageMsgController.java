package com.rongdu.cashloan.manage.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.rongdu.cashloan.cl.domain.OperatorVoices;
import com.rongdu.cashloan.cl.domain.UserContacts;
import com.rongdu.cashloan.cl.domain.UserMessages;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.OperatorVoicesService;
import com.rongdu.cashloan.cl.service.UserContactsService;
import com.rongdu.cashloan.cl.service.UserMessagesService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.domain.User;
import com.rongdu.cashloan.core.service.CloanUserService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
* 代理用户信息Controller
* 
* @author lyang
* @version 1.0.0
* @date 2017-02-28 10:24:45
* Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
* 官方网站：www.erongdu.com
* 
* 未经授权不得进行修改、复制、出售及商业使用
*/
@Scope("prototype")
@Controller
public class ManageMsgController extends ManageBaseController{

	@Resource
	private UserContactsService userContactsService;
	@Resource
	private UserMessagesService userMessagesService;
	@Resource
	private OperatorVoicesService operatorVoicesService;
	@Resource
	private CloanUserService cloanUserService;
	@Resource
	private ClBorrowService clBorrowService;
	
	/**
	 * 通讯录查询
	 * @param userId
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/msg/listContacts.htm")
	public void listContacts(
			@RequestParam(value="phone",required=false) String phone,
			@RequestParam(value="name",required=false) String name,
			@RequestParam(value="userId") long userId,
			@RequestParam(value="borrowId",required=false) Long borrowId,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		if (borrowId!=null) {
			userId = clBorrowService.findByPrimary(borrowId).getUserId();
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId",userId);
		map.put("phone",phone);
		map.put("name",name);
		Page<UserContacts> page = userContactsService.listContactsByContion(map,current,pageSize);
		Map<String, Object> data = new HashMap<>();
		data.put("list", page.getResult());
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 短信查询
	 * @param userId
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/msg/listMessages.htm")
	public void listMessages(
			@RequestParam(value="userId") long userId,
			@RequestParam(value="borrowId",required=false) Long borrowId,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		if (borrowId!=null) {
			userId = clBorrowService.findByPrimary(borrowId).getUserId();
		}
		Page<UserMessages> page = userMessagesService.listMessages(userId,current,pageSize);
		Map<String, Object> data = new HashMap<>();
		data.put("list", page.getResult());
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 通话记录查询
	 * @param userId
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/msg/listRecords.htm")
	public void listRecords(@RequestParam(value="userId") long userId,
			@RequestParam(value="borrowId",required=false) Long borrowId,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		if (borrowId!=null) {
			userId = clBorrowService.findByPrimary(borrowId).getUserId();
		}
		PageHelper.startPage(current, pageSize);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		User user = cloanUserService.getById(userId);
		String phone = "";
		if (user != null) {
			phone = user.getLoginName();
		}
		params = new HashMap<String, Object>();
		params.put("phoneNum", phone);
		Page<OperatorVoices> page =  operatorVoicesService.findPage(params, current, pageSize);
		Map<String, Object> data = new HashMap<>();
		data.put("list", page.getResult());
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
}
