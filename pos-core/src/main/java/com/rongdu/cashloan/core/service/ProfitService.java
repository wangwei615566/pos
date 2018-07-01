package com.rongdu.cashloan.core.service;

import java.util.List;
import java.util.Map;

public interface ProfitService {

    Map<String,Object> extensionAndGoodsCount(Long userId);

    List<Map<String,Object>> extensionList(Long userId);

    List<Map<String,Object>>goodsDetaiUser(Long userId);
}
