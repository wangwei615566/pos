package com.pos.api.controller;

import com.czwx.cashloan.core.model.ProfitLevel;
import com.czwx.cashloan.core.model.User;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.common.web.controller.BaseController;
import com.rongdu.cashloan.core.service.CloanUserService;
import com.rongdu.cashloan.core.service.ProfitLevelService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Controller
public class MemberController{

    @Resource
    private ProfitLevelService profitLevelService;
    @Resource
    private CloanUserService userService;

    @RequestMapping("api/level/all/center.htm")
    public void memberCenter(@RequestParam(value = "userId")Long userId,HttpServletResponse response){
        Map<String, Object> param = new HashMap<>();
        List<ProfitLevel> profitLevels = profitLevelService.listSelect(param);
        User user = userService.selectByPrimaryKey(userId);
        for (ProfitLevel p:profitLevels
             ) {
            if (user.getLevelId()>=p.getId()) {
                p.setFlag(true);
            }else {
                p.setFlag(false);
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
        result.put(Constant.RESPONSE_DATA, profitLevels);
        result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
        ServletUtils.writeToResponse(response, result);
    }

    @RequestMapping("api/level/all/test.htm")
    public void test(HttpServletResponse response){
        Map<String, Object> param = new HashMap<>();

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
        result.put(Constant.RESPONSE_DATA, "111");
        result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
        ServletUtils.writeToResponse(response, result);
    }
}