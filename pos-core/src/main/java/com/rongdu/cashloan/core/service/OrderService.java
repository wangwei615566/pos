package com.rongdu.cashloan.core.service;

import com.czwx.cashloan.core.model.Order;

import java.util.Map;

public interface OrderService {
    Order createOrderMember(Long userId,Long goodsId);
    boolean payOrder(Map<String,Object> param);
}
