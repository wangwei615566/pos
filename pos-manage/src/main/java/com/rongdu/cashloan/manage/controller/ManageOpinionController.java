package com.rongdu.cashloan.manage.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.Opinion;
import com.rongdu.cashloan.cl.model.OpinionModel;
import com.rongdu.cashloan.cl.service.OpinionService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.DateUtil;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.system.domain.SysUser;

 /**
 * 意见反馈表Controller
 * 
 * @author lyang
 * @version 1.0.0
 * @date 2017-02-14 11:30:57
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Scope("prototype")
@Controller
public class ManageOpinionController extends ManageBaseController {

	@Resource
	private OpinionService opinionService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/mine/opinion/page.htm")
	public void page(
			@RequestParam(value = "searchParams", required = false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		Map<String,Object> searchMap = new HashMap<>();
        if (!StringUtils.isEmpty(searchParams)){
        	searchMap = JsonUtil.parse(searchParams, Map.class);
        }
		Page<OpinionModel> page = opinionService.page(searchMap, current, pageSize);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("list", page);
		resultMap.put(Constant.RESPONSE_DATA, data);
		resultMap.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		resultMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		resultMap.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,resultMap);
	}
	
	@RequestMapping(value="/modules/manage/mine/opinion/view.htm")
	public void view(@RequestParam(value = "id",required=false) Long id) throws Exception{
		Map<String,Object> resultMap = new HashMap<String, Object>();
		Opinion opinion = opinionService.getById(id);
		if(opinion !=null ){
			resultMap.put(Constant.RESPONSE_DATA, opinion);
			resultMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			resultMap.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		}else{
			resultMap.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			resultMap.put(Constant.RESPONSE_CODE_MSG, "获取意见反馈失败");
		}
		ServletUtils.writeToResponse(response,resultMap);
	}
	
	@RequestMapping(value="/modules/manage/mine/opinion/confirm.htm", method = RequestMethod.POST)
	public void opinionConfirm(
			@RequestParam(value = "id",required=true) Long id,
			@RequestParam(value = "feedback",required=true) String feedback
			) throws Exception{
		Map<String,Object> paramMap = new HashMap<String, Object>();
		Map<String,Object> resultMap = new HashMap<String, Object>();
		SysUser user = getLoginUser(request);
		if (user!=null) {
			paramMap.put("id", id);
			paramMap.put("sysUserId", getLoginUser(request).getId());
			paramMap.put("feedback", feedback);
			paramMap.put("confirmTime", DateUtil.dateStr3(DateUtil.getNow()));
			paramMap.put("state", OpinionModel.STATE_CONFIRMED);
			int result = opinionService.updateSelective(paramMap);
			if(result == 1){
				resultMap.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				resultMap.put(Constant.RESPONSE_CODE_MSG, "处理成功");
			}else{
				resultMap.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				resultMap.put(Constant.RESPONSE_CODE_MSG, "处理失败");
			}
		}else {
			resultMap.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			resultMap.put(Constant.RESPONSE_CODE_MSG, "处理失败,登陆已失效");
		}
		ServletUtils.writeToResponse(response, resultMap);
	}
}
