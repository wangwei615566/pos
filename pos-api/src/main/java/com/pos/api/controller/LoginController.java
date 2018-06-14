package com.pos.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pos.api.user.bean.AppAbsActionWrapper;
import com.pos.api.user.bean.AppSessionBean;
import com.pos.api.user.service.AppDbSession;
import com.pos.api.user.service.DBService;
import com.pos.api.user.service.MybatisService;
import com.pos.api.user.service.SmsService;
import com.pos.api.user.service.UserService;
import com.rongdu.cashloan.core.common.util.MD5;
import com.rongdu.cashloan.core.common.util.StringUtil;

/**
 * Created by lsk on 2016/7/27.
 */
@Scope("prototype")
@Controller
@SuppressWarnings({ "unchecked", "rawtypes" })
@RequestMapping("/api/act/user")
public class LoginController {

	@Resource
	private DBService dbService;
	@Resource
	private MybatisService mybatisService;
	@Resource
	private AppDbSession appDbSession;

	@Resource(name = "clUserService_")
	private UserService userService;

	@Resource
	private SmsService smsService;

	@RequestMapping("login")
	public void login(final HttpServletRequest request,
			HttpServletResponse response, final String loginName,
			final String loginPwd) {
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				return userService.login(request, loginName, loginPwd);
			}
		};
	}

	@RequestMapping("autoLogin")
	public void autoLogin(final HttpServletRequest request,
			final HttpServletResponse response, final String refresh_token) {
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				Map result = new LinkedHashMap();

				if (StringUtil.isEmpty(refresh_token)) {
					Map ret = new LinkedHashMap();
					ret.put("success", false);
					ret.put("msg", "非法请求");
					return ret;
				}

//				if (Math.abs(new Date().getTime() - subtime) > 60000) {
//					Map ret = new LinkedHashMap();
//					ret.put("success", false);
//					ret.put("msg", "请确认参数subtime是否正确");
//					return ret;
//				}

				/*
				 * String verify = MD5.encode(Global.getValue("app_key") +
				 * refreshToken + subtime); if
				 * (!verify.equalsIgnoreCase(signMsg)) { Map ret = new
				 * LinkedHashMap(); ret.put("success", false); ret.put("msg",
				 * "签名未通过"); return ret; } else {
				 */
				Object obj = appDbSession.autoLogin(request, refresh_token);

				if (!(obj instanceof AppSessionBean)) {
					return obj;
				}

				AppSessionBean bean = (AppSessionBean) obj;
				Date loginTime = new Date();
                dbService.update("update cl_user set login_time=? where uuid=?", loginTime, refresh_token);//设置当前时间为登录时间

				result.put("success", true);
				result.put("data", bean.getFront());
				result.put("msg", "成功自动登录");
				return result;
			}
		};
	}

	// FORGET_PWD
	// REGISTER_VCODE

	/**
	 * @param request
	 * @param response
	 * @param phone
	 *   REGISTER_VCODE FORGET_PWD
	 */
	@RequestMapping("fetchSmsVCode")
	public void fetchSmsVCode(final HttpServletRequest request,
			final HttpServletResponse response, final String phone) {
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				if (StringUtil.isEmpty(phone)) {
					Map ret = new LinkedHashMap();
					ret.put("success", false);
					ret.put("msg", "手机号码不能为空");
					return ret;
				}
				smsService.sendSmsByTpl(request, phone, "vcode", randomNum(4));
				Map ret = new LinkedHashMap();
				ret.put("success", true);
				ret.put("msg", "验证码已发送");
				return ret;
			}
		};
	}

	private static String randomNum(int len) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	@RequestMapping("register")
	public void register(final HttpServletRequest request,
			final HttpServletResponse response, 
			final String loginName,
			final String loginPwd, 
			final String vcode,
			final String invitationCode, 
			final String client,
			final String registerCoordinate,
			final String registerAddr, 
			final String signMsg,
			final String markChannel
			) {
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				String channelCode = "jyqb"; // 设置渠道为“玖印钱包”
				Map result = userService.registerUser(request, loginName,
						loginPwd, vcode, invitationCode, registerCoordinate,
						registerAddr, client, signMsg, channelCode ,markChannel,null,null);
				if ((Boolean) result.get("success")) {
					result = userService.login(request, loginName, loginPwd);
					result.put("msg", result.get("msg"));
				}
				return result;
			}
		};
	}

	@RequestMapping("wxRegister")
	public void wxRegister(final HttpServletRequest request,
			final HttpServletResponse response, final String loginName,
			final String loginPwd, final String vcode,
			final String invitationCode,
			final String channelCode, final String registerCoordinate,
			final String registerAddr, final String signMsg,final String markChannel,
			final String platform
			) {
		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isNotBlank(platform)) {
			map.put("platform", platform);
		}
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				Map result = userService.registerUser(request, loginName,
						loginPwd.toUpperCase(), vcode, invitationCode, registerCoordinate,
						registerAddr, "h5", signMsg,channelCode,markChannel,null,null);
				if ((Boolean) result.get("success")) {
//					result = userService.login(request, loginName, loginPwd,
//							signMsg,blackBox);
					result.put("msg", "注册成功!");
				}
				return result;
			}
		};
	}

	/**
	 * 短信注册，不需要密码
	 * @param request
	 * @param response
	 * @param loginName
	 * @param vcode
	 * @param invitationCode
	 * @param channelCode
	 * @param registerCoordinate
	 * @param registerAddr
	 * @param signMsg
	 * @param markChannel
	 * @param platform
	 */
	@RequestMapping("smsRegister")
	public void smsRegister(final HttpServletRequest request,
						   final HttpServletResponse response, final String loginName,
						   final String vcode,
						   final String invitationCode,
						   final String channelCode, final String registerCoordinate,
						   final String registerAddr, final String signMsg,final String markChannel,
						   final String platform
	) {
		if (StringUtils.isNotBlank(platform)) {
		}
		int code = (int) (Math.random() * 900000) + 100000;
		final String pwd = "jy"+code;
		final String loginPwd = MD5.md5(pwd);
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				final String blackBox = "";
				Map result = userService.registerUser(request, loginName,
						loginPwd.toUpperCase(), vcode, invitationCode, registerCoordinate,
						registerAddr, "h5", signMsg,channelCode,markChannel,"sms",pwd);
				if ((Boolean) result.get("success")) {
//					result = userService.login(request, loginName, loginPwd,
//							signMsg,blackBox);
					result.put("msg", "注册成功!");
				}
				return result;
			}
		};
	}


	@RequestMapping("login/forgetPwd.htm")
	public void forgetPwd(final HttpServletRequest request,
			HttpServletResponse response, final String phone,
			final String newPwd, final String vcode, final String signMsg) {
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				return userService.forgetPwd(phone, newPwd, vcode, signMsg);
			}
		};
	}

	@RequestMapping("validateSmsCode")
	public void validateSmsCode(final HttpServletRequest request,
			HttpServletResponse response, final String phone,
			final String type, final String vcode, final String signMsg) {
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				if (StringUtil.isEmpty(phone) || StringUtil.isEmpty(vcode)
						|| StringUtil.isEmpty(signMsg)) {
					Map ret = new LinkedHashMap();
					ret.put("success", false);
					ret.put("msg", "非法参数");
					return ret;
				}

				/*
				 * String _signMsg = MD5.encode(Global.getValue("app_key") +
				 * phone + vcode); if (!_signMsg.equalsIgnoreCase(signMsg)) {
				 * Map ret = new LinkedHashMap(); ret.put("success", false);
				 * ret.put("msg", "签名验签不通过"); return ret; }
				 */

				String msg = smsService.validateSmsCode(phone, type, vcode);
				Map ret = new LinkedHashMap();
				ret.put("success", true);
				ret.put("msg", msg);

				Map data = new LinkedHashMap();
				data.put("pass", msg == null);
				ret.put("data", data);
				return ret;
			}
		};

	}

	@RequestMapping("isPhoneExists")
	public void isPhoneExists(final HttpServletRequest request,
			HttpServletResponse response, final String phone) {
		new AppAbsActionWrapper(response) {
			@Override
			public Object doAction() {
				boolean exists = mybatisService.queryRec("usr.isPhoneExists",
						phone) != null;
				Map ret = new LinkedHashMap();
				ret.put("success", true);

				Map data = new LinkedHashMap();
				data.put("isExists", exists ? "20" : "10");
				ret.put("data", data);
				ret.put("msg", exists ? "该手机号码已存在" : "该手机不存在，可注册");
				return ret;
			}
		};
	}
}
