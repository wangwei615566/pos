package com.rongdu.cashloan.manage.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.DrainagePlatform;
import com.rongdu.cashloan.cl.model.DrainagePlatformModel;
import com.rongdu.cashloan.cl.service.DrainagePlatformService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.common.web.controller.BaseController;
/**
 * 导流合作平台
 * @author taodd
 *
 */
@Controller
@Scope("prototype")
public class DrainagePlatformController extends BaseController {
	@Resource
	private DrainagePlatformService drainagePlatformService;
	/**
	 * 引流合作平台列表
	 * @param searchParams
	 * @param current
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/drainage/platform/data/list.htm",method=RequestMethod.POST)
	public void drainageOrderList(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<DrainagePlatformModel> page = drainagePlatformService.drainagePlatformList(params, current, pageSize);
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
	@RequestMapping(value="/modules/manage/drainage/platform/addAndUpdate.htm",method={RequestMethod.GET,RequestMethod.POST})
	public void addAndUpdate(@RequestParam(value="id") Long id,
			@RequestParam(value="name") String name,
			@RequestParam(value="platform") String platform,
			@RequestParam(value="ranking") Long ranking,
			@RequestParam(value="status") String status){
		Map<String,Object> result = new HashMap<String,Object>();
		if (ranking<1) {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "排名必须大于0");
			ServletUtils.writeToResponse(response,result);
			return;
		}
		if ("create".equalsIgnoreCase(status)) {
			DrainagePlatform drainagePlatform = new DrainagePlatform();
			drainagePlatform.setName(name);
			drainagePlatform.setPlatform(platform);
			drainagePlatform.setRanking(ranking);
			drainagePlatform.setState(DrainagePlatform.STATE_ENABLE);
			drainagePlatform.setCreatTime(new Date());
			drainagePlatform.setUpdateTime(new Date());
			int msg = drainagePlatformService.save(drainagePlatform);
			if (msg>0) {
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "添加成功");
			} else {
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "添加失败");
			}
		} else if ("update".equals(status)) {
			DrainagePlatform updatePlatform = new DrainagePlatform();
			updatePlatform.setId(id);
			updatePlatform.setName(name);
			updatePlatform.setPlatform(platform);
			updatePlatform.setRanking(ranking);
			updatePlatform.setUpdateTime(new Date());
			int msg = drainagePlatformService.update(updatePlatform);
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
	 * 启用平台
	 * @param id
	 */
	@RequestMapping(value="/modules/manage/drainage/platform/enable.htm",method={RequestMethod.GET,RequestMethod.POST})
	public void enable(@RequestParam(value="id") Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		DrainagePlatform drainagePlatform = drainagePlatformService.getById(id);
		drainagePlatform.setState(DrainagePlatform.STATE_ENABLE);
		int msg = drainagePlatformService.update(drainagePlatform);
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
	@RequestMapping(value="/modules/manage/drainage/platform/disable.htm",method={RequestMethod.GET,RequestMethod.POST})
	public void disable(@RequestParam(value="id") Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		DrainagePlatform drainagePlatform = drainagePlatformService.getById(id);
		drainagePlatform.setState(DrainagePlatform.STATE_DISABLE);
		int msg = drainagePlatformService.update(drainagePlatform);
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
