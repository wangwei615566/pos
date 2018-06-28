package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.ProfitLevel;

public interface ProfitLevelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProfitLevel record);

    int insertSelective(ProfitLevel record);

    ProfitLevel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProfitLevel record);

    int updateByPrimaryKey(ProfitLevel record);
}