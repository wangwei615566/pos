package com.rongdu.cashloan.manage.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
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

import tool.util.StringUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.BankCard;
import com.rongdu.cashloan.cl.domain.Channel;
import com.rongdu.cashloan.cl.domain.UserAuth;
import com.rongdu.cashloan.cl.domain.UserEducationInfo;
import com.rongdu.cashloan.cl.domain.UserEmerContacts;
import com.rongdu.cashloan.cl.domain.Zhima;
import com.rongdu.cashloan.cl.model.InviteBorrowModel;
import com.rongdu.cashloan.cl.model.UserAuthModel;
import com.rongdu.cashloan.cl.model.UserEducationInfoModel;
import com.rongdu.cashloan.cl.service.BankCardService;
import com.rongdu.cashloan.cl.service.ChannelService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.UserAuthService;
import com.rongdu.cashloan.cl.service.UserEducationInfoService;
import com.rongdu.cashloan.cl.service.UserEmerContactsService;
import com.rongdu.cashloan.cl.service.UserInviteService;
import com.rongdu.cashloan.cl.service.ZhimaService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.domain.User;
import com.rongdu.cashloan.core.domain.UserBaseInfo;
import com.rongdu.cashloan.core.domain.UserOtherInfo;
import com.rongdu.cashloan.core.model.BlackUserModel;
import com.rongdu.cashloan.core.model.CloanUserModel;
import com.rongdu.cashloan.core.model.ManagerUserModel;
import com.rongdu.cashloan.core.service.CloanUserService;
import com.rongdu.cashloan.core.service.UserBaseInfoService;
import com.rongdu.cashloan.core.service.UserOtherInfoService;
import com.rongdu.cashloan.system.permission.annotation.RequiresPermission;
import com.rongdu.creditrank.cr.model.CreditModel;
import com.rongdu.creditrank.cr.service.CreditService;

import credit.CreditRequest;
import credit.Header;

 /**
 * 用户记录Controller
 * 
 * @author jdd
 * @version 1.0.0
 * @date 2017-02-21 13:39:06
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
@Controller
@Scope("prototype")
public class ManageUserController extends ManageBaseController{
	private static final Logger logger = LoggerFactory.getLogger(ManageUserController.class);
	@Resource
	private CloanUserService cloanUserService;
	@Resource
	private UserAuthService authService;
	@Resource
	private UserBaseInfoService userBaseInfoService;
	@Resource
	private BankCardService bankCardService;
	@Resource
	private UserEmerContactsService userEmerContactsService;
	@Resource
	private UserInviteService userInviteService;
	@Resource
	private UserOtherInfoService userOtherInfoService;
	@Resource
	private UserEducationInfoService userEducationService;
	@Resource
	private CreditService creditService;
	@Resource
	private ZhimaService zhimaService;
	@Resource
	private ChannelService channelService;
	@Resource
	private ClBorrowService clBorrowService;
		
	/**
	 *用户信息列表
	 * @param searchParams
	 * @param currentPage
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/cl/cluser/list.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:cl:cluser:list",name = "用户信息列表")
	public void list(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<CloanUserModel> page = cloanUserService.listUser(params,currentPage,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 用户详细信息
	 * @param userId
	 * @param currentPage
	 * @param pageSize
	 * @throws Exception 
	 */
	@RequestMapping(value="/modules/manage/cl/cluser/detail.htm",method={RequestMethod.GET,RequestMethod.POST})   
	@RequiresPermission(code = "modules:manage:cl:cluser:detail",name = "用户详细信息")
	public void detail(@RequestParam(value = "userId") Long userId,@RequestParam(value = "borrowId",required=false) Long borrowId) throws Exception{
		if (borrowId!=null) {
			userId = clBorrowService.findByPrimary(borrowId).getUserId();
		}
		String serverHost = Global.getValue("server_host");
		HashMap<String, Object> map = new HashMap<String,Object>();
		User user = cloanUserService.getById(userId);
		if (user != null && user.getId() != null) {
			//用户基本信息
			ManagerUserModel model = userBaseInfoService.getBaseModelByUserId(userId);
			model.setLivingImg(model.getLivingImg()!=null?serverHost +"/readFile.htm?path="+ URLEncoder.encode(model.getLivingImg(), "UTF-8"):"");
			model.setFrontImg(model.getFrontImg()!=null?serverHost +"/readFile.htm?path="+ URLEncoder.encode(model.getFrontImg(), "UTF-8"):"");
			model.setBackImg(model.getBackImg()!=null?serverHost +"/readFile.htm?path="+ URLEncoder.encode(model.getBackImg(), "UTF-8"):"");
			model.setOcrImg(model.getOcrImg()!=null?serverHost +"/readFile.htm?path="+ URLEncoder.encode(model.getOcrImg(), "UTF-8"):"");
			
			if (StringUtil.isNotBlank(model.getWorkingImg())) {
				String workImgStr = model.getWorkingImg();
				List<String> workImgList = Arrays.asList(workImgStr.split(";"));
				for (int i = 0; i < workImgList.size(); i++) {
					String workImg = workImgList.get(i);
					workImgList.set(i, serverHost +"/readFile.htm?path="+ workImg);
				}
				map.put("workImgArr", workImgList);
			}
			
			//银行卡信息
			BankCard bankCard=bankCardService.getBankCardByUserId(user.getId());
			if (null != bankCard) {
				model.setBank(bankCard.getBank());
				model.setCardNo(bankCard.getCardNo());
				model.setBankPhone(bankCard.getPhone());
			}
			
			Channel cl = channelService.getById(user.getChannelId());
			if (cl!=null) {
				model.setChannelName(cl.getName());
			}
			
			//芝麻分
			Zhima zm = zhimaService.findByUserId(userId);
			if (zm!=null&&zm.getScore()>0) {
				model.setScore(zm.getScore().toString());
			}
			map.put("userbase", model);
			
			// 构造查询条件Map
			HashMap<String, Object> paramMap = new HashMap<String,Object>();
			paramMap.put("userId",user.getId());
			
			// 认证信息
			UserAuth authModel = authService.getUserAuth(paramMap);
			map.put("userAuth", authModel);
			
			// 联系人信息
			List<UserEmerContacts> infoModel = userEmerContactsService.getUserEmerContactsByUserId(paramMap);
			map.put("userContactInfo", infoModel);
			
			// 用户其他账号信息
			UserOtherInfo otherInfo = userOtherInfoService.getInfoByUserId(user.getId());
			map.put("userOtherInfo", otherInfo);
			
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, map);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 用户认证信息列表
	 * @param search
	 * @param currentPage
	 * @param pageSize
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/modules/manage/cl/cluser/authlist.htm",method={RequestMethod.GET,RequestMethod.POST})
	@RequiresPermission(code = "modules:manage:cl:cluser:authlist",name = "用户认证信息列表")
	public void authlist(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize){
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<UserAuthModel> page = authService.listUserAuth(params, currentPage, pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * @description  查询黑名单用户列表
	 * @param response
	 * @param request
	 * @param currentPage
	 * @param pageSize
	 * @param search
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/cl/cluser/credit/list.htm")
	@RequiresPermission(code = "modules:manage:cl:cluser:credit:list",name = "查询用户列表")
	public void page(
			@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int currentPage,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		Map<String,Object> searchMap = JsonUtil.parse(searchParams, Map.class);
		Page<BlackUserModel> page = userBaseInfoService.findBlackUser(searchMap, currentPage, pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 查询邀请用户借款记录
	 * @param userId
	 * @param current
	 * @param pageSize
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/invite/listInviteBorrow.htm")
	public void listInviteBorrow(
			@RequestParam(value="userId") long userId,
			@RequestParam(value = "borrowId",required=false) Long borrowId,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		if (borrowId!=null) {
			userId = clBorrowService.findByPrimary(borrowId).getUserId();
		}
		Page<InviteBorrowModel> page = userInviteService.listInviteBorrow(userId,current,pageSize);
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
	 * 添加和取消黑名单
	 * @param id
	 * @param state
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/user/updateState.htm")
	public void updateState(
			@RequestParam(value="id") long id,
			@RequestParam(value="state") String state,
			@RequestParam(value="blackReason",required=false) String blackReason,
			@RequestParam(value="blackDesc",required=false) String blackDesc) throws Exception {
		String blackContent = "";
		String blackDescible = "";
		try{
			blackContent = blackReason;
			blackDescible = blackDesc;
		} catch(Exception e){
			e.printStackTrace();
		}
		int msg = userBaseInfoService.updateState(id,state,blackContent,blackDescible);
		Map<String,Object> result = new HashMap<String,Object>();
		if (msg<0) {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "修改失败");
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "修改成功");		
		}
		ServletUtils.writeToResponse(response,result);
	}
	
	 /**
     * 天行 学信查询
     * @throws Exception
     */
	@RequestMapping(value = "/modules/manage/user/educationRequest.htm")
    public void apiEducationRequest(
    		@RequestParam(value="name") String name,
    		@RequestParam(value="idCard") String idCard) throws Exception{
    	final String APIKEY = Global.getValue("apikey");
		final String SECRETKEY = Global.getValue("secretkey");
    	String url = Global.getValue("tx_apihost");
        final String channelNo = Global.getValue("tx_channelNo");
        final String interfaceName = Global.getValue("tx_interfaceName");
        
        long timestamp = new Date().getTime();
        Header header = new Header(APIKEY, channelNo, interfaceName, timestamp);

        CreditRequest creditRequest = new CreditRequest(url, header);

        Map<String, Object> payload = new HashMap<>();

        payload.put("name", name);
        payload.put("idCard", idCard);

        creditRequest.setPayload(payload);

        creditRequest.signByKey(SECRETKEY);

        String resultStr = creditRequest.request();

        JSONObject resultJson = JSONObject.parseObject(resultStr);
        
        
        Map<String,Object> map = new HashMap<>();
        map.put("loginName", name);
        User user = cloanUserService.findByPhone(name);
        
        UserBaseInfo ubi = new UserBaseInfo();
        if (user!=null) {
        	map = new HashMap<>();
        	map.put("userId", user.getId());
        	ubi = userBaseInfoService.findSelective(map);
		}
        
        int msg = 0;
        Map<String,Object> result = new HashMap<String,Object>();
        if (ubi!=null&&resultJson.getInteger("code")==200) {
        	JSONObject resJson = JSONObject.parseObject(StringUtil.isNull(resultJson.get("res")));
        	logger.info(StringUtil.isNull(resJson));
			UserEducationInfo ue = new UserEducationInfo();
			ue.setUserId(ubi.getUserId());
			ue.setEducationType(resJson.getString("educationType"));
			ue.setProfession(resJson.getString("profession"));
			ue.setMatriculationTime(resJson.getString("matriculationTime"));
			ue.setGraduateSchool(resJson.getString("graduateSchool"));
			ue.setGraduationTime(resJson.getString("graduationTime"));
			ue.setGraduationConclusion(resJson.getString("graduationConclusion"));
			ue.setEducationBackground(resJson.getString("educationBackground"));
			ue.setState("20");
			msg = userEducationService.save(ue);
		}
		logger.info(resultJson.getString("message"));
		if (msg>0) {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "操作成功");
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "操作失败");
			
		}
		ServletUtils.writeToResponse(response,result);
    }
	
	/**
	 * 修改学历信息
	 * @param uei
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/user/updateEducation.htm")
	public void updateEducation(UserEducationInfo uei) throws Exception {
		int msg = userEducationService.update(uei);
		Map<String,Object> result = new HashMap<String,Object>();
		if (msg<0) {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "修改失败");
		}else {
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "修改成功");
			
		}
		ServletUtils.writeToResponse(response,result);
	}
	
	/**
	 * 查询学历列表
	 * @param uei
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/modules/manage/user/educationList.htm")
	public void educationList(
			@RequestParam(value="search",required=false) String search,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws Exception {
		Map<String,Object> searchMap = JsonUtil.parse(search, Map.class);
		Page<UserEducationInfoModel> page = userEducationService.list(searchMap,current,pageSize);
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> data = new HashMap<>();
		data.put("list", page.getResult());
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
}
