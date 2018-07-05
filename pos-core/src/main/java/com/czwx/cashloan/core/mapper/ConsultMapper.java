package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.Consult;

public interface ConsultMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Consult record);

    int insertSelective(Consult record);

    Consult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Consult record);

    int updateByPrimaryKey(Consult record);
}