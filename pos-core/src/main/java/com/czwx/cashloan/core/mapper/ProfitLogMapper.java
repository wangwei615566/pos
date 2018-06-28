package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.ProfitLog;

public interface ProfitLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProfitLog record);

    int insertSelective(ProfitLog record);

    ProfitLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProfitLog record);

    int updateByPrimaryKey(ProfitLog record);
}