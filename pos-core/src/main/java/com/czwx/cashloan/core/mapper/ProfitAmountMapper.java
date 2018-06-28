package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.ProfitAmount;

public interface ProfitAmountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProfitAmount record);

    int insertSelective(ProfitAmount record);

    ProfitAmount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProfitAmount record);

    int updateByPrimaryKey(ProfitAmount record);
}