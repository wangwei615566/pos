package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.UserShippingAddr;

public interface UserShippingAddrMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserShippingAddr record);

    int insertSelective(UserShippingAddr record);

    UserShippingAddr selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserShippingAddr record);

    int updateByPrimaryKey(UserShippingAddr record);
}