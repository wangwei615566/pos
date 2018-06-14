/**
 *
 *
 * @author rd
 * @version 1.0.0.0
 * @date 2016年12月01日 下午16:01:55
 * Copyright 杭州融都科技股份有限公司 资金托管系统  All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
package com.rongdu.cashloan.system.mapper;

import java.util.List;
import java.util.Map;

import com.rongdu.cashloan.core.common.mapper.BaseMapper;
import com.rongdu.cashloan.core.common.mapper.RDBatisDao;
import com.rongdu.cashloan.system.domain.SysPerm;

@RDBatisDao
public interface SysPermMapper extends BaseMapper<SysPerm,Long>  {

    SysPerm selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysPerm record);
    
    List<SysPerm> listByUserName(String userName);
    
    List<SysPerm> selectAll();
    
	List<SysPerm> listByRoleId(Long roleId);

	List<Map<String, Object>> fetchAll();
}