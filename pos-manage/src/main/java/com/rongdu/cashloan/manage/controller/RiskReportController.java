package com.rongdu.cashloan.manage.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.czwx.cafintech.entity.ApplyInfoDO;
import com.czwx.cafintech.service.CafintechService;
import com.rongdu.cashloan.cl.service.BorrowProgressService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.ServletUtils;

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
public class RiskReportController extends ManageBaseController{

	@Resource
	private CafintechService cafintechService;
	@Resource
	private BorrowProgressService borrowProgressService;
	
	/**
	 * 风控报告信息查询
	 * @param borrowId
	 * @throws Exception
	 */
	@RequestMapping(value = "/modules/manage/review/riskReport.htm")
	public void findResult(
			@RequestParam(value="borrowId") long borrowId
			) throws Exception{
		String orderNo = borrowProgressService.findOrderNo(borrowId);
		ApplyInfoDO applyInfoDO = cafintechService.getApplyInfo(orderNo);
//		ApplyInfoDO applyInfoDO = cafintechService.getApplyInfo("01677599630");
		Map<String,Object> data =  new HashMap<>();
		data.put("applyInfoDO", applyInfoDO);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
		ServletUtils.writeToResponse(response,result);
	}
	
}
