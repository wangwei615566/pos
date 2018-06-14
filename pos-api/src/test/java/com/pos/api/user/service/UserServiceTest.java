package com.pos.api.user.service;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

import com.alibaba.fastjson.JSON;
import com.rongdu.cashloan.core.domain.User;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/config/spring/*-beans.xml"})
public class UserServiceTest {
	private final static Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
	@Resource
	MybatisService mybatisService;
	
	@Test
	public void testRegisterUser() {
		//userService.registerUser(request, phone, pwd, vcode, invitationCode, registerCoordinate, registerAddr, regClient, signMsg, channelCode, markChannel, registration, passWord)
	}

	@Test
	public void testForgetPwd() {
		//fail("Not yet implemented");
	}

	@Test
	public void testLogin() throws FileNotFoundException {
		Map user = mybatisService.queryRec("usr.queryUserByLoginName", "15327732322");
		logger.info(JSON.toJSONString(user));
	}

}
