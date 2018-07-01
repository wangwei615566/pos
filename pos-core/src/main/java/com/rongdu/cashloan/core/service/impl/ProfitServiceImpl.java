package com.rongdu.cashloan.core.service.impl;

import com.czwx.cashloan.core.mapper.ProfitAgentMapper;
import com.rongdu.cashloan.core.service.ProfitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("profitService")
public class ProfitServiceImpl implements ProfitService{
    @Resource
    private ProfitAgentMapper profitAgentMapper;

    /**
     * 推广数及代理数
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> extensionAndGoodsCount(Long userId) {
        return profitAgentMapper.extensionAndGoodsCount(userId);
    }

    @Override
    public List<Map<String, Object>> extensionList(Long userId) {
        return profitAgentMapper.extensionList(userId);
    }

    @Override
    public List<Map<String, Object>> goodsDetaiUser(Long userId) {
        List<Map<String, Object>> data = profitAgentMapper.goodsList(userId);
        for (Map<String, Object> map:data
             ) {
            Long goodsId = (long)map.get("id");
            Map<String, Object> param = new HashMap<>();
            param.put("userId",userId);
            param.put("goodsId",goodsId);
            List<Map<String, Object>> maps = profitAgentMapper.goodsListDetailUser(param);
            map.put("users",maps);
        }
        return data;
    }
}
