package com.rongdu.cashloan.manage.controller;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.UrgeMemberModifyLog;
import com.rongdu.cashloan.cl.service.UrgeMemberModifyLogService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 催款订单催收员变更记录表相关接口Controller
 *
 * @author Leon
 * Copyright 武汉成长无限网络科技有限公司  arc All Rights Reserved
 * 未经授权不得进行修改、复制、出售及商业使用
*/
@Scope("prototype")
@Controller
public class ManageUrgeMemberModifyController extends ManageBaseController {

   @Resource
   private UrgeMemberModifyLogService urgeMemberModifyLogService;

   /**
    * 催款订单催收员变更记录表
    * @param searchParams
    * @param current
    * @param pageSize
    */
   @SuppressWarnings("unchecked")
   @RequestMapping(value="/modules/manage/borrow/repay/urge/member/modify/list.htm",method={RequestMethod.GET,RequestMethod.POST})
   public void list(
           @RequestParam(value="searchParams",required=false) String searchParams,
           @RequestParam(value = "current") int current,
           @RequestParam(value = "pageSize") int pageSize){
       Map<String,Object> result = new HashMap<String,Object>();
       Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
       Page<UrgeMemberModifyLog> page = urgeMemberModifyLogService.list(params, current, pageSize);
       result.put(Constant.RESPONSE_DATA, page);
       result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
       result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
       result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
       ServletUtils.writeToResponse(response,result);
   }

}
