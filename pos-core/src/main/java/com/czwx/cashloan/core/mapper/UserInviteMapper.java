package com.czwx.cashloan.core.mapper;

import com.czwx.cashloan.core.model.UserInvite;

public interface UserInviteMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserInvite record);

    int insertSelective(UserInvite record);

    UserInvite selectByPrimaryKey(Long id);

    UserInvite selectByUserId(Long id);

    int updateByPrimaryKeySelective(UserInvite record);

    int updateByPrimaryKey(UserInvite record);
}