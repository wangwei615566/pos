package com.rongdu.cashloan.manage.controller;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.Channel;
import com.rongdu.cashloan.cl.model.ChannelCountModel;
import com.rongdu.cashloan.cl.model.ChannelModel;
import com.rongdu.cashloan.cl.service.ChannelService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * 渠道信息Controller
 * 
 * @author gc                            
 * @version 1.0.0
 * @date 2017-03-03 10:52:07
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Scope("prototype")
@Controller
public class ChannelController extends ManageBaseController {

	@Resource
	private ChannelService channelService;

	/**
	 * 保存
	 * @param code
	 * @param name
	 * @param linker
	 * @param phone
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/promotion/channel/save.htm", method = RequestMethod.POST)
	public void save(@RequestParam(value="code") String code,
			@RequestParam(value="name") String name,
			@RequestParam(value="linker") String linker,
			@RequestParam(value="phone") String phone) throws Exception {
		Channel channel=new Channel();
		channel.setCode(code);
		channel.setLinker(linker);
		channel.setName(name);
		channel.setPhone(phone);
		boolean flag = channelService.save(channel);

		Map<String, Object> result = new HashMap<String, Object>();
		if (flag) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_FAIL);
		}
		ServletUtils.writeToResponse(response, result);
	}

	/**
	 * 渠道信息列表页查看
	 * 
	 * @param searchParams
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/promotion/channel/page.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void page(
			@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		Map<String, Object> searchMap = new HashMap<>();
		if (!StringUtils.isEmpty(searchParams)) {
			searchMap = JsonUtil.parse(searchParams, Map.class);
		}

		Page<ChannelModel> page = channelService.page(current, pageSize,searchMap);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response, result);
	}
	
	/**
	 * 修改
	 * @param id
	 * @param code
	 * @param name
	 * @param linker
	 * @param phone
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/promotion/channel/update.htm", method = RequestMethod.POST)
	public void update(
			@RequestParam(value="id") Long id,
			@RequestParam(value="code") String code,
			@RequestParam(value="name") String name,
			@RequestParam(value="linker") String linker,
			@RequestParam(value="phone") String phone) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		paramMap.put("code", code);
		paramMap.put("name", name);
		paramMap.put("linker", linker);
		paramMap.put("phone", phone);
		boolean flag = channelService.update(paramMap);
		Map<String, Object> result = new HashMap<String, Object>();
		if (flag) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_FAIL);
		}
		ServletUtils.writeToResponse(response, result);
	}

	
	/**
	 * 渠道信息修改状态
	 */
	@RequestMapping(value = "/modules/manage/promotion/channel/updateState.htm", method = RequestMethod.POST)
	public void updateState(@RequestParam(value="id") Long id,
					@RequestParam(value="state") String state) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		paramMap.put("state", state);
		boolean flag = channelService.update(paramMap);
		Map<String, Object> result = new HashMap<String, Object>();
		if (flag) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_FAIL);
		}
		ServletUtils.writeToResponse(response, result);
	}
	
	/**
	 * 统计渠道用户信息
	 * 
	 * @param searchParams
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/promotion/channel/channelUserList.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void channelUserList(
			@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		Map<String, Object> searchMap = new HashMap<>();
		if (!StringUtils.isEmpty(searchParams)) {
			searchMap = JsonUtil.parse(searchParams, Map.class);
		}

		Map<String, Object> map = new HashMap<>();
		Page<ChannelCountModel> page = channelService.clientCountChannel(searchMap);
		List<HashMap<String, Object>> borrowChannel = channelService.borrowChannel(searchMap);
		List<HashMap<String, Object>> loanOutChannel = channelService.loanOutChannel(searchMap);
		List<HashMap<String, Object>> repaymentChannel = channelService.repaymentChannel(searchMap);
		List<HashMap<String, Object>> overdueChannel = channelService.overdueChannel(searchMap);
		if(page != null && page.size() > 0) {
			for (ChannelCountModel channelCountModel : page) {
				if (borrowChannel != null && borrowChannel.size() > 0) {
					for (HashMap<String, Object> borrowMap : borrowChannel) {
						if (channelCountModel.getCode().equals(borrowMap.get("code"))) {
							channelCountModel.setBorrowMember(borrowMap.get("borrowMember").toString());
							channelCountModel.setBorrowCount(borrowMap.get("borrowCount").toString());
							channelCountModel.setBorrowAmout(borrowMap.get("borrowAmout").toString() == null ? "0.00" : borrowMap.get("borrowAmout").toString());
						}
					}
				}

				if (loanOutChannel != null && loanOutChannel.size() > 0) {
					for (HashMap<String, Object> loanOutMap : loanOutChannel) {
						if (channelCountModel.getCode().equals(loanOutMap.get("code"))) {
							channelCountModel.setPayCount(loanOutMap.get("payCount").toString());
							channelCountModel.setPayAccount(loanOutMap.get("payAccount").toString() == null ? "0.00" : loanOutMap.get("payAccount").toString());
						}
					}
				}

				if (repaymentChannel != null && repaymentChannel.size() > 0) {
					for (HashMap<String, Object> repaymentMap : repaymentChannel) {
						if (channelCountModel.getCode().equals(repaymentMap.get("code"))) {
							channelCountModel.setRepayAmount(repaymentMap.get("repayAmount").toString() == null ? 0.00 : Double.valueOf(repaymentMap.get("repayAmount").toString()));
						}
					}
				}

				if (overdueChannel != null && overdueChannel.size() > 0) {
					for (HashMap<String, Object> overdueMap : overdueChannel) {
						if (channelCountModel.getCode().equals(overdueMap.get("code"))) {
							channelCountModel.setPenaltyAmount(overdueMap.get("penaltyAmount").toString() == null ? 0.00 : Double.valueOf(overdueMap.get("penaltyAmount").toString()));
						}
					}
				}
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response, result);
	}
	
	
	
	
}
