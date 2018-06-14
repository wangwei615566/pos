package com.rongdu.cashloan.manage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.Page;
import com.rongdu.cashloan.cl.domain.UrgeRepayProgress;
import com.rongdu.cashloan.cl.model.UrgeRepayProgressModel;
import com.rongdu.cashloan.cl.service.CollectionPayNotifyService;
import com.rongdu.cashloan.cl.service.CollectionRepayService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.RdPage;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.common.web.controller.BaseController;
import com.rongdu.cashloan.system.domain.SysUser;
import com.rongdu.cashloan.system.service.SysUserRoleService;

/**
 * 催收系统接口
 * Created by wang.wei on 2017/11/7.
 */
@Scope("prototype")
@Controller
public class CollectionRepayController extends BaseController {
	
    private static final Logger logger = LoggerFactory.getLogger(CollectionRepayController.class);
    
    @Resource
    private CollectionRepayService collectionRepayService;
    @Resource
    private CollectionPayNotifyService collectionPayNotifyService;
    @Resource
    private SysUserRoleService sysUserRoleService;
    
    /**
     * 催收审批流程
     * @param state
     * @param reason
     */
    @RequestMapping(value ="/modules/manage/collection/check.htm",method=RequestMethod.POST)
    public void check(@RequestParam(value="state") String state,@RequestParam(value="dueId") Long dueId,@RequestParam(value="reason",required=false) String reason){
    	SysUser user = (SysUser) request.getSession().getAttribute("SysUser");
    	Map<String,Object> param = new HashMap<>();
    	param.put("state", state);
    	param.put("dueId", dueId);
    	if(StringUtils.isNotBlank(reason)){
    		param.put("remark", reason);
    	}
    	int flag = collectionRepayService.update(param);
    	Map<String,Object> result = new HashMap<String,Object>();
    	if(flag < 1){
    		result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
    		result.put(Constant.RESPONSE_CODE_MSG, "更新失败");
    		ServletUtils.writeToResponse(response,result);
    	} else {
    		UrgeRepayProgress urgeRepayProgress = collectionRepayService.findSelective(param);
    		param.put("repayType", urgeRepayProgress.getRepayType());
    		param.put("money", urgeRepayProgress.getMoney());
    		param.put("applyId", urgeRepayProgress.getApplyId());
    		String stateCode = collectionPayNotifyService.collectionRepayNotify(param);
    		if("200".equals(stateCode)){
    			result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
    			result.put(Constant.RESPONSE_CODE_MSG, "更新成功");
    			ServletUtils.writeToResponse(response,result);   			
    		} else {
    			result.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
        		result.put(Constant.RESPONSE_CODE_MSG, "更新失败");
        		ServletUtils.writeToResponse(response,result);
    		}
    	}
    }
    
    /**
     * 审批展示的列表
     * @param serviceName
     * @throws ServiceException 
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value ="/modules/manage/collection/list.htm",method=RequestMethod.POST)
    public void list(@RequestParam(value="searchParams",required=false) String searchParams,
			@RequestParam(value = "current") int current,
			@RequestParam(value = "pageSize") int pageSize) throws ServiceException{
    	SysUser sysUser = (SysUser)request.getSession().getAttribute("SysUser"); 
    	List<String> nids = sysUserRoleService.roleNameMap(sysUser);
		String roleYes = "0";
		if (nids.contains("system")||nids.contains("financialStaff")) {
			roleYes="1";
		}
		Map<String, Object> params = JsonUtil.parse(searchParams, Map.class);
		Page<UrgeRepayProgressModel> page = (Page<UrgeRepayProgressModel>) collectionRepayService.listModel(params,
				current, pageSize);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constant.RESPONSE_DATA, page);
		result.put("roleYes", roleYes);
		result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
		result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
		result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
		ServletUtils.writeToResponse(response, result);
    }
    
}

