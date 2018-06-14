package com.rongdu.cashloan.system.domain;

import java.util.Date;

/**
 *
 * @author rd
 * @version 1.0.0.0
 * @date 2016年12月02日 下午14:56:41
 * Copyright 杭州融都科技股份有限公司 arc  All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
public class SysRolePerm {
	/**
	 * 主键
	 */ 
	private Long id;

	/**
	 * 角色ID
	 */ 
	private Integer roleId;

	/**
	 * 权限ID
	 */ 
	private Integer permId;

	/**
	 * 添加时间
	 */ 
	private Date addTime;

	/**
	 * 添加人
	 */ 
	private String addUser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getPermId() {
		return permId;
	}

	public void setPermId(Integer permId) {
		this.permId = permId;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getAddUser() {
		return addUser;
	}

	public void setAddUser(String addUser) {
		this.addUser = addUser;
	}




}