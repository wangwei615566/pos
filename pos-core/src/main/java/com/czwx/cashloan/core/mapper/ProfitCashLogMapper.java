package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.ProfitCashLog;

public interface ProfitCashLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProfitCashLog record);

    int insertSelective(ProfitCashLog record);

    ProfitCashLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProfitCashLog record);

    int updateByPrimaryKey(ProfitCashLog record);
}