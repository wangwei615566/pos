package com.rongdu.cashloan.core.service;

import com.czwx.cashloan.core.model.Goods;

import java.util.List;
import java.util.Map;

public interface GoodsService {

    List<Goods> listMember(Long userId);
    List<Goods> listSelect(Map<String,Object> param);
}
