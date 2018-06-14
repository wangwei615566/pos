package com.rongdu.cashloan.manage.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
/**
 * 
 * @author taodd
 *
 */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.common.web.controller.BaseController;
import com.rongdu.cashloan.core.model.SurplusOrderModel;
import com.rongdu.cashloan.core.model.SurplusOrderPlatformModel;
import com.rongdu.cashloan.core.service.SurplusOrderPlatformService;
import com.rongdu.cashloan.system.domain.SysAccessCode;
import com.rongdu.cashloan.system.domain.SysUser;
import com.rongdu.cashloan.system.model.SysAccessCodeModel;
import com.rongdu.cashloan.system.permission.annotation.RequiresPermission;
/**
 * 管理台尾单合作平台controller
 * @author taodd
 *
 */
@Controller
@Scope("prototype")
public class SurplusOrderPlatformController extends BaseController{
	@Resource
	private SurplusOrderPlatformService surplusOrderPlatformService;
	/**
	 * 尾量合作平台列表
	 * @param searchParams
	 * @param current
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/surplusOrder/surplusOrderDate/list.htm",method=RequestMethod.POST)
	public void surplusOrderList(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<SurplusOrderModel> page = surplusOrderPlatformService.listSurplusOrder(params, current, pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	/**
	 * 新增合作平台或修改合作平台
	 * @param sysUserId
	 * @param code
	 * @param id
	 * @param time
	 * @param status
	 */
	@RequestMapping(value="/modules/manage/surplusOrderPlatform/save.htm",method={RequestMethod.GET,RequestMethod.POST})
	public void add(@RequestParam(value="id") Long id,
			@RequestParam(value="creatId") Long creatId,
			@RequestParam(value="name") String name,
			@RequestParam(value="channelUrl") String channelUrl,
			@RequestParam(value="ranking") Long ranking,
			@RequestParam(value="introduce") String introduce,
			@RequestParam(value="status") String status){
		Map<String,Object> result = new HashMap<String,Object>();
		if (ranking<1) {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "排名必须大于0");
			ServletUtils.writeToResponse(response,result);
			return;
		}
		if ("create".equalsIgnoreCase(status)) {
			SurplusOrderPlatformModel formModel = new SurplusOrderPlatformModel();
			formModel.setId(creatId);
			formModel.setName(name);
			formModel.setChannelUrl(channelUrl);
			formModel.setIntroduce(introduce);
			formModel.setRanking(ranking);
			formModel.setState(SurplusOrderPlatformModel.STATE_ENABLE);
			formModel.setCreatTime(new Date());
			formModel.setUpdateTime(new Date());
			int msg = surplusOrderPlatformService.save(formModel);
			if (msg>0) {
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "添加成功");
			} else {
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "添加失败");
			}
		} else if ("update".equals(status)) {
			SurplusOrderPlatformModel updateFormModel = new SurplusOrderPlatformModel();
			updateFormModel.setId(id);
			updateFormModel.setChannelUrl(channelUrl);
			updateFormModel.setName(name);
			updateFormModel.setIntroduce(introduce);
			updateFormModel.setRanking(ranking);
			updateFormModel.setUpdateTime(new Date());
			int msg = surplusOrderPlatformService.update(updateFormModel);
			if (msg>0) {
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "修改成功");
			} else {
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "修改失败");
			}
		}
		ServletUtils.writeToResponse(response,result);
	}
	/**
	 * 平台名称列表
	 */
	@RequestMapping(value="/modules/manage/surplusOrder/listName.htm",method={RequestMethod.GET,RequestMethod.POST})
	public void listUserName(){
		List<SurplusOrderPlatformModel> list = surplusOrderPlatformService.listUserName();
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, list);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	/**
	 * 启用平台
	 * @param id
	 */
	@RequestMapping(value="/modules/manage/surplusOrderPlatform/enable.htm",method={RequestMethod.GET,RequestMethod.POST})
	public void enable(@RequestParam(value="id") Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		SurplusOrderPlatformModel formModel = surplusOrderPlatformService.getById(id);
		formModel.setState(SurplusOrderPlatformModel.STATE_ENABLE);
		int msg = surplusOrderPlatformService.updateState(formModel);
        if (msg > 0) {
        	result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "启用成功");
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "启用失败");
		}
        ServletUtils.writeToResponse(response,result);
	}
	
	
	/**
	 * 禁用平台
	 * @param id
	 */
	@RequestMapping(value="/modules/manage/surplusOrderPlatform/disable.htm",method={RequestMethod.GET,RequestMethod.POST})
	public void disable(@RequestParam(value="id") Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		SurplusOrderPlatformModel formModel = surplusOrderPlatformService.getById(id);
		formModel.setState(SurplusOrderPlatformModel.STATE_DISABLE);
		int msg = surplusOrderPlatformService.updateState(formModel);
        if (msg > 0) {
        	result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "禁用成功");
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "禁用失败");
		}
        ServletUtils.writeToResponse(response,result);
	}
}
