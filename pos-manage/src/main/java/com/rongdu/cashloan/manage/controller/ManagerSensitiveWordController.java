package com.rongdu.cashloan.manage.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.rongdu.cashloan.cl.domain.SensitiveWord;
import com.rongdu.cashloan.cl.service.SensitiveWordService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.ServletUtils;

/**
 * 敏感词管理
 * @author create by fangguochao on 2017-10-09
 *
 */
@Controller
@Scope("prototype")
public class ManagerSensitiveWordController extends ManageBaseController{

	private static final Logger logger  = LoggerFactory.getLogger(ManagerSensitiveWordController.class);
	
	@Resource
	private SensitiveWordService sensitiveWordService;
	

	/**
	 * 保存
	 * @param content 敏感词内容
	 * @param state 敏感词分类
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/sensitive/save.htm", method = RequestMethod.POST)
	public void save(@RequestParam(value="content") String content,
			@RequestParam(value="type") String type) {
		//先查询保存内容是否重复
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("content", content);
		paramMap.put("type", type);
		SensitiveWord exist = sensitiveWordService.findSelective(paramMap);
		Map<String, Object> result = new HashMap<String, Object>();
		if(exist != null){
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "该敏感词数据库中已存在");
		} else {
			SensitiveWord sensitiveWord=new SensitiveWord();
			sensitiveWord.setContent(content);
			sensitiveWord.setType(type);
			sensitiveWord.setCreateTime(new Date());
			sensitiveWord.setUpdateTime(new Date());
			int flag = sensitiveWordService.save(sensitiveWord);		
			if (flag > 0) {
				result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
			} else {
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_FAIL);
			}
		}
		ServletUtils.writeToResponse(response, result);
	}
	
	/**
	 * 修改
	 * @param id 敏感词id
	 * @param content 敏感词内容
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/sensitive/update.htm", method = RequestMethod.POST)
	public void update(
			@RequestParam(value="id") Long id,
			@RequestParam(value="content") String content) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		//根据id查询敏感词
		SensitiveWord sensitive = sensitiveWordService.findByPrimary(id);
		if(sensitive == null){
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_FAIL);
		} else {
			//根据修改的敏感词内容和分类查询是否重复
			Map<String,Object> paramMap = new HashMap<>();
			paramMap.put("content", content);
			paramMap.put("type", sensitive.getType());
			paramMap.put("id", id);
			SensitiveWord exist = sensitiveWordService.findSelective(paramMap);
			if(exist != null){
				result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
				result.put(Constant.RESPONSE_CODE_MSG, "该敏感词数据库中已存在");
			} else {
				//修改敏感词
				paramMap.put("id", id);
				paramMap.put("updateTime",new Date());
				int flag = sensitiveWordService.updateSelective(paramMap);
				if (flag > 0) {
					result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
					result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
				} else {
					result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
					result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_FAIL);
				}
			}
		}
		ServletUtils.writeToResponse(response, result);
	}
	
	/**
	 * 查询敏感词列表
	 * 
	 * @param searchParams
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/sensitive/list.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void list(@RequestParam(value="type") String type) throws Exception {
		Map<String, Object> searchMap = new HashMap<>();
		searchMap.put("type", type);
		List<SensitiveWord> listSelective = sensitiveWordService.listSelective(searchMap);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, listSelective);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response, result);
	}
	
	/**
	 * 删除敏感词
	 */
	@RequestMapping(value = "/modules/manage/sensitive/delete.htm", method = RequestMethod.GET)
	public void delete(@RequestParam(value="id") Long id) throws Exception {
		int flag = sensitiveWordService.deleteById(id);
		Map<String, Object> result = new HashMap<String, Object>();
		if (flag > 0) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_SUCCESS);
		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, Constant.OPERATION_FAIL);
		}
		ServletUtils.writeToResponse(response, result);
	}
	
	/**
	 * 通讯录敏感词列表
	 * @param userId
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/msg/sensitive/listContacts.htm", method = {RequestMethod.POST,RequestMethod.GET})
	public void listContacts(
			@RequestParam(value="userId") long userId,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		Map<String,Object> result  = sensitiveWordService.listContacts(userId,current,pageSize);
		ServletUtils.writeToResponse(response,result);
	}

}
