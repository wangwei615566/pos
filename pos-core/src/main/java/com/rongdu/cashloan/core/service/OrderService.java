package com.rongdu.cashloan.core.service;

import com.czwx.cashloan.core.model.Order;
import com.czwx.cashloan.core.model.OrderDetail;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrderMember(Long userId,Long goodsId);

    boolean payOrder(Map<String,Object> param);

    List<Order> listSelective(Map<String,Object> param);

    List<OrderDetail> orderDetail(Long orderId);
}
