package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.User;
import com.rongdu.cashloan.core.common.mapper.RDBatisDao;

import java.util.Map;

@RDBatisDao
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User findSelective(Map<String,Object> param);
}