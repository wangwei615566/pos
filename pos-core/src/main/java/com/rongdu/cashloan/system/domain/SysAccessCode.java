package com.rongdu.cashloan.system.domain;

import java.io.Serializable;
import java.util.Date;

import tool.util.DateUtil;

/**
 * 访问码实体
 * 
 * @author dufy
 * @version 1.0.0
 * @date 2017-03-24 17:37:49
 * Copyright 杭州融都科技股份有限公司  arc All Rights Reserved
 * 官方网站：www.erongdu.com
 * 
 * 未经授权不得进行修改、复制、出售及商业使用
 */
 public class SysAccessCode implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 2小时
     */
    public static final String TWO_HOUR = "01";
    
    /**
     * 12小时
     */
    public static final String HALF_DAY = "02";
    
    /**
     * 一天
     */
    public static final String ONE_DAY = "03";
    
    /**
     * 两天
     */
    public static final String TWO_DAY = "04";
    
    /**
     * 七天
     */
    public static final String SEVEN_DAY = "05";
    
    /**
     * 1个月
     */
    public static final String ONE_MOUNTH = "06";
    
    /**
     * 3个月
     */
    public static final String THREE_MOUNTH = "07";
    
    /**
     * 6个月
     */
    public static final String SIX_MOUNTH = "08";
    /**
     * 12个月
     */
    public static final String TWELVE_MOUNTH = "09";
    
	/**
	 * 主键Id
	 */
	private Long id;

	/**
	 * 用户标识
	 */
	private Long sysUserId;

	/**
	 * 访问码 
	 */
	private String code;

	/**
	 * 状态 10 启用 20 禁用
	 */
	private String state;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 过期时间
	 */
	private Date exceedTime;
	
	public SysAccessCode(){
		super();
	}
	
	public SysAccessCode(long sysUserId, String code, String state, Date exceedTime){
		super();
		this.sysUserId = sysUserId;
		this.code = code;
		this.state = state;
		this.createTime = DateUtil.getNow();
		this.exceedTime = exceedTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(Long sysUserId) {
		this.sysUserId = sysUserId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getExceedTime() {
		return exceedTime;
	}

	public void setExceedTime(Date exceedTime) {
		this.exceedTime = exceedTime;
	}
	
}