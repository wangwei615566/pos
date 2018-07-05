package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.AppInfo;
import com.rongdu.cashloan.core.common.mapper.RDBatisDao;

@RDBatisDao
public interface AppInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppInfo record);

    int insertSelective(AppInfo record);

    AppInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppInfo record);

    int updateByPrimaryKey(AppInfo record);
}