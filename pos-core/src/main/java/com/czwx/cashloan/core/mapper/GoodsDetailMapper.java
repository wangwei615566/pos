package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.GoodsDetail;

public interface GoodsDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsDetail record);

    int insertSelective(GoodsDetail record);

    GoodsDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsDetail record);

    int updateByPrimaryKeyWithBLOBs(GoodsDetail record);

    int updateByPrimaryKey(GoodsDetail record);
}