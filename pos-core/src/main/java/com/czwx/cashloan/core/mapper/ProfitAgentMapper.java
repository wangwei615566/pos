package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.ProfitAgent;

public interface ProfitAgentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProfitAgent record);

    int insertSelective(ProfitAgent record);

    ProfitAgent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProfitAgent record);

    int updateByPrimaryKey(ProfitAgent record);
}