package com.rongdu.cashloan.manage.controller;

import com.rongdu.cashloan.cl.model.ManageBorrowTestModel;
import com.rongdu.cashloan.cl.service.BorrowRepayLogService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.OperatorReqLogService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.service.CloanUserService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Controller
public class ManageBorrowTest extends ManageBaseController{

	@Resource
	private ClBorrowService clBorrowService;
	@Resource
	private CloanUserService userService;
	@Resource
	private OperatorReqLogService operatorReqLogService;
	@Resource
	private CloanUserService cloanUserService;
	@Resource
	private BorrowRepayLogService borrowRepayLogService;
	
	@RequestMapping(value = "/modules/manage/user/list.htm")
	public void list()throws Exception{
		List<ManageBorrowTestModel> list = clBorrowService.seleteUser();
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("list", list);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constant.RESPONSE_DATA, data);
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "申请成功");
		ServletUtils.writeToResponse(response,result);
	}
	
	
	@RequestMapping(value = "/modules/manage/borrow/apply.htm")
	public void apply(
			@RequestParam(value="amount") double amount,
			@RequestParam(value="timeLimit") String timeLimit,
			@RequestParam(value="userId") long userId,
			@RequestParam(value="cardId") long cardId
			) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		
		Borrow borrow = new Borrow(userId, amount, timeLimit, cardId, "", "", "",ServletUtils.getIpAddress(request));
		borrow = clBorrowService.rcBorrowApply(borrow,"0000","1","");
		if(borrow!=null && borrow.getId()>0){
			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "申请成功");
		} else {
			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "申请失败");
		}
		ServletUtils.writeToResponse(response,result);
	}
	 
}
