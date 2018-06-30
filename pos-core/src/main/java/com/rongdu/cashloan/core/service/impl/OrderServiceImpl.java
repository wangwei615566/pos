package com.rongdu.cashloan.core.service.impl;

import com.czwx.cashloan.core.mapper.GoodsMapper;
import com.czwx.cashloan.core.mapper.OrderMapper;
import com.czwx.cashloan.core.mapper.UserMapper;
import com.czwx.cashloan.core.model.Goods;
import com.czwx.cashloan.core.model.Order;
import com.czwx.cashloan.core.model.User;
import com.rongdu.cashloan.core.common.exception.BussinessException;
import com.rongdu.cashloan.core.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private UserMapper userMapper;
    @Override
    public Order createOrderMember(Long userId,Long goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        String orderNo = getOrderNo();
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setGoodsNum(1);
        order.setAmount(goods.getPrice());
        order.setState((byte)1);
        order.setUpdateTime(new Date());
        order.setCreateTime(new Date());
        orderMapper.insertSelective(order);
        return order;
    }

    /**
     * 支付会员费接口
     * @param param
     * @return
     */
    @Override
    public boolean payOrder(Map<String, Object> param) {
        long userId = (long)param.get("userId");
        String orderNo = param.get("orderNo").toString();
        Map<String, Object> paramOrder = new HashMap<>();
        paramOrder.put("userId",userId);
        paramOrder.put("orderNo",orderNo);
        Order order = orderMapper.findSelective(paramOrder);
        if (order!=null && order.getState() !=1){
            throw  new BussinessException("订单不存在或状态有误");
        }
        //支付


        order.setState((byte)4);
        int i = orderMapper.updateByPrimaryKey(order);
        long levelId = (long)param.get("levelId");
        User user = userMapper.selectByPrimaryKey(userId);
        user.setLevelId(levelId);
        int j = userMapper.updateByPrimaryKeySelective(user);
        if (i>0 && j>0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 生成订单号
     * @return
     */
    public static String getOrderNo() {
        Random random = new Random();
        String reqOrderNo = "";
        for (int i = 0; i < 9; i++) {
            int a = random.nextInt(10);
            reqOrderNo += a;
        }
        return reqOrderNo;
    }
}
