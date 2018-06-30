package com.rongdu.cashloan.core.service.impl;

import com.czwx.cashloan.core.mapper.GoodsMapper;
import com.czwx.cashloan.core.mapper.UserMapper;
import com.czwx.cashloan.core.model.Goods;
import com.czwx.cashloan.core.model.User;
import com.rongdu.cashloan.core.service.GoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private UserMapper userMapper;
    @Override
    public List<Goods> listMember(Long userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("type",2);
        List<Goods> goods = goodsMapper.listSelect(param);
        User user = userMapper.selectByPrimaryKey(userId);
        if (user.getLevelId() == 2) goods.get(0).setFlag(false);
        if (user.getLevelId() == 3) {
            goods.get(0).setFlag(false);
            goods.get(1).setFlag(false);
        }
        return goods;
    }

    @Override
    public List<Goods> listSelect(Map<String, Object> param) {
        return goodsMapper.listSelect(param);
    }
}
