package com.pos.api.controller;

import com.czwx.cashloan.core.model.Order;
import com.czwx.cashloan.core.model.OrderDetail;
import com.czwx.cashloan.core.model.ProfitLevel;
import com.czwx.cashloan.core.model.User;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.common.web.controller.BaseController;
import com.rongdu.cashloan.core.service.OrderService;
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
public class OrderController extends BaseController {
    @Resource
    private OrderService orderService;

    /**
     * 订单列表接口
     *
     * @param userId
     */
    @RequestMapping("api/order/list.htm")
    public void listOrder(@RequestParam(value = "userId") Long userId,
                          @RequestParam(value = "state", required = false) Integer state) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("state", state);
        List<Order> orders = orderService.listSelective(param);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
        result.put(Constant.RESPONSE_DATA, orders);
        result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
        ServletUtils.writeToResponse(response, result);
    }

    /**
     * 订单详情接口
     *
     * @param orderId
     */
    @RequestMapping("api/order/detail.htm")
    public void orderDetail(@RequestParam(value = "orderId") Long orderId) {
        List<OrderDetail> orderDetails = orderService.orderDetail(orderId);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
        result.put(Constant.RESPONSE_DATA, orderDetails);
        result.put(Constant.RESPONSE_CODE_MSG, "获取成功");
        ServletUtils.writeToResponse(response, result);
    }

    /**
     * 订单详情注册码添加
     *
     * @param orderDetailId
     * @param code
     */
    @RequestMapping("api/order/detail/code.htm")
    public void orderDetailCode(@RequestParam(value = "orderDetailId") Long orderDetailId,
                                @RequestParam(value = "code") String code) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(orderDetailId);
        orderDetail.setCode(code);
        int i = orderService.updateOrderDetail(orderDetail);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constant.RESPONSE_CODE, i>0?Constant.SUCCEED_CODE_VALUE:Constant.FAIL_CODE_VALUE);
        result.put(Constant.RESPONSE_CODE_MSG, i>0?"添加成功":"添加失败");
        ServletUtils.writeToResponse(response, result);
    }
}