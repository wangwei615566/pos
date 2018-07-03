package com.rongdu.cashloan.core.service.impl;

import com.czwx.cashloan.core.mapper.GoodsMapper;
import com.czwx.cashloan.core.mapper.OrderDetailMapper;
import com.czwx.cashloan.core.mapper.OrderMapper;
import com.czwx.cashloan.core.mapper.UserMapper;
import com.czwx.cashloan.core.model.Goods;
import com.czwx.cashloan.core.model.Order;
import com.czwx.cashloan.core.model.OrderDetail;
import com.czwx.cashloan.core.model.User;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.rongdu.cashloan.core.common.exception.BussinessException;
import com.rongdu.cashloan.core.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Override
    public Order createOrderMember(Long userId,Long goodsId) {
        Date date = new Date();
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        String orderNo = getOrderNo();
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setGoodsNum(1);
        order.setAmount(goods.getPrice());
        order.setState((byte)1);
        order.setUpdateTime(date);
        order.setCreateTime(date);
        orderMapper.insertSelective(order);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setGoodsId(goodsId);
        orderDetail.setGoodsNum(1);
        orderDetail.setOrderId(order.getId());
        orderDetail.setCreateTime(date);
        orderDetail.setUpdateTime(date);
        orderDetailMapper.insertSelective(orderDetail);
        return order;
    }

    @Override
    public Order selectByPrimaryKey(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        return order;
    }

    @Override
    public int updateBySelect(Map<String,Object> map) {
        return orderMapper.updateBySelect(map);
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

    @Override
    public List<Order> listSelective(Map<String, Object> param) {
        return orderMapper.listSelective(param);
    }

    @Override
    public List<OrderDetail> orderDetail(Long orderId) {
        Map<String, Object> param = new HashMap<>();
        param.put("orderId",orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.listSelective(param);
        for (OrderDetail d:orderDetails
             ) {
            Goods goods = goodsMapper.selectByPrimaryKey(d.getGoodsId());
            d.setGoods(goods);
        }
        return orderDetails;
    }

    @Override
    public Page<Order> pageList(Map<String, Object> param, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Order> orderList = orderMapper.listSelective(param);
        return (Page<Order>) orderList;
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
