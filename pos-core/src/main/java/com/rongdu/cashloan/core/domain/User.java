package com.rongdu.cashloan.core.domain;

import java.io.Serializable;
import java.util.Date;


 public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键Id
    */
    private Long id;
    
    /**
     * 唯一标识uuid
     */
     private String uuid;

    /**
    * 登录名
    */
    private String loginName;

    /**
    * 登录密码
    */
    private String loginPwd;

    /**
    * 上次登录密码修改时间
    */
    private Date loginpwdModifyTime;

    /**
    * 交易密码
    */
    private String tradePwd;

    /**
    * 上次交易密码修改时间
    */
    private Date tradepwdModifyTime;
    
    /**
     * 注册时间
     */
     private Date registTime;

     /**
     * 注册客户端
     */
     private String registerClient;

    /**
    * 邀请码
    */
    private String invitationCode;
    
	/**
	 * 渠道
	 */
    private Long channelId;

	public String getMarkChannel() {
		return markChannel;
	}

	public void setMarkChannel(String markChannel) {
		this.markChannel = markChannel;
	}

	/**
	 * APP市场渠道
	 */
	private String markChannel;

    /**
    * 代理等级 ，1一级，2二级，3普通用户
    */
    private Integer level;
    
    /**
     * 卡牛用户唯一标识
     */
    private String knid;
    
    /**
     * 注册ip
     */
    private String registerIp;
    
    /**
     * 登陆ip
     */
    private String loginIp;
    
    /**
     * 登录时间
     */
    private Date loginTime;
    
    /**
     * 卡牛评分
     */
    private Integer score;
    
    /**
     * 卡牛活动标志
     */
    private String activityCode;

	/**
	 * 引流平台id
	 */
	private Long drainageId;
	/**
	 * 获取主键Id
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置主键Id
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取唯一标识uuid
	 * @return 唯一标识uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * 设置唯一标识uuid
	 * @param 唯一标识uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * 获取登录名
	 * @return loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * 设置登录名
	 * @param loginName
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * 获取登录密码
	 * @return loginPwd
	 */
	public String getLoginPwd() {
		return loginPwd;
	}

	/**
	 * 设置登录密码
	 * @param loginPwd
	 */
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	/**
	 * 获取上次登录密码修改时间
	 * @return loginpwdModifyTime
	 */
	public Date getLoginpwdModifyTime() {
		return loginpwdModifyTime;
	}

	/**
	 * 设置上次登录密码修改时间
	 * @param loginpwdModifyTime
	 */
	public void setLoginpwdModifyTime(Date loginpwdModifyTime) {
		this.loginpwdModifyTime = loginpwdModifyTime;
	}

	/**
	 * 获取交易密码
	 * @return tradePwd
	 */
	public String getTradePwd() {
		return tradePwd;
	}

	/**
	 * 设置交易密码
	 * @param tradePwd
	 */
	public void setTradePwd(String tradePwd) {
		this.tradePwd = tradePwd;
	}

	/**
	 * 获取上次交易密码修改时间
	 * @return tradepwdModifyTime
	 */
	public Date getTradepwdModifyTime() {
		return tradepwdModifyTime;
	}

	/**
	 * 设置上次交易密码修改时间
	 * @param tradepwdModifyTime
	 */
	public void setTradepwdModifyTime(Date tradepwdModifyTime) {
		this.tradepwdModifyTime = tradepwdModifyTime;
	}

	/**
	 * 获取注册时间
	 * @return registTime
	 */
	public Date getRegistTime() {
		return registTime;
	}

	/**
	 * 设置注册时间
	 * @param registTime
	 */
	public void setRegistTime(Date registTime) {
		this.registTime = registTime;
	}

	/**
	 * 获取注册客户端
	 * @return registerClient
	 */
	public String getRegisterClient() {
		return registerClient;
	}

	/**
	 * 设置注册客户端
	 * @param registerClient
	 */
	public void setRegisterClient(String registerClient) {
		this.registerClient = registerClient;
	}

	/**
	 * 获取邀请码
	 * @return invitationCode
	 */
	public String getInvitationCode() {
		return invitationCode;
	}

	/**
	 * 设置邀请码
	 * @param invitationCode
	 */
	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	/**
	 * 获取渠道
	 * @return channelId
	 */
	public Long getChannelId() {
		return channelId;
	}

	/**
	 * 设置渠道
	 * @param channelId
	 */
	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	/**
	 * 获取代理等级，1一级，2二级，3普通用户
	 * @return level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * 设置代理等级，1一级，2二级，3普通用户
	 * @param level
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * 获取卡牛用户唯一标识
	 * @return
	 */
	public String getKnid() {
		return knid;
	}

	/**
	 * 设置卡牛用户唯一标识
	 * @param knid
	 */
	public void setKnid(String knid) {
		this.knid = knid;
	}

	/**
	 * 获取注册ip
	 * @return
	 */
	public String getRegisterIp() {
		return registerIp;
	}

	/**
	 * 设置注册ip
	 * @param registerIp
	 */
	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	/**
	 * 获取登录ip
	 * @return
	 */
	public String getLoginIp() {
		return loginIp;
	}

	/**
	 * 设置登陆ip
	 * @param loginIp
	 */
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	/**
	 * 获取登录时间
	 * @return
	 */
	public Date getLoginTime() {
		return loginTime;
	}

	/**
	 * 设置登录时间
	 * @param loginTime
	 */
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * 获取卡牛评分
	 * @return
	 */
	public Integer getScore() {
		return score;
	}

	/**
	 * 设置卡妞评分
	 * @param score
	 */
	public void setScore(Integer score) {
		this.score = score;
	}

	/**
	 * 获取卡牛活动标志
	 * @return
	 */
	public String getActivityCode() {
		return activityCode;
	}

	/**
	 * 设置卡牛活动标志
	 * @param activityCode
	 */
	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public Long getDrainageId() {
		return drainageId;
	}

	public void setDrainageId(Long drainageId) {
		this.drainageId = drainageId;
	}
}