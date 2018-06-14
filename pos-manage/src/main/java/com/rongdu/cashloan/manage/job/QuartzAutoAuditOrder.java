package com.rongdu.cashloan.manage.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.czwx.cafintech.service.CafintechService;
import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.domain.Channel;
import com.rongdu.cashloan.cl.domain.UserContacts;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ChannelService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.UserContactsService;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.domain.User;
import com.rongdu.cashloan.core.service.CloanUserService;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;

import tool.util.BeanUtil;
import tool.util.DateUtil;

@Component
@Lazy(value = false)
public class QuartzAutoAuditOrder implements StatefulJob{
	
	private static final Logger logger = Logger.getLogger(QuartzAutoAuditOrder.class);

	/**
	 * 自动审核待机审订单
	 * 
	 * @throws ServiceException
	 */
	public String autoAuditBorrow() throws ServiceException {
		CloanUserService cloanUserService = (CloanUserService) BeanUtil.getBean("cloanUserService");
		ClBorrowService clBorrowService = (ClBorrowService) BeanUtil.getBean("clBorrowService");
		BorrowRepayService clBorrowRepayService = (BorrowRepayService) BeanUtil.getBean("borrowRepayService");
		UserContactsService userContactsService = (UserContactsService) BeanUtil.getBean("clUserContactsService");
		CafintechService cafintechService = (CafintechService) BeanUtil.getBean("cafintechService");
		ChannelService channelService =(ChannelService) BeanUtil.getBean("channelService");
		logger.info("进入待机审订单...");
		String quartzRemark = null;
		int succeed = 0;
		int fail = 0;
		int total = 0;
		try {
			Map<String,Object> searchMap = new HashMap<String,Object>();
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -6);
			searchMap.put("lteCreateTime",calendar.getTime());
			searchMap.put("state", "10");
			List<Borrow> list = clBorrowService.findBorrowByMap(searchMap);
			int size = list.size();
			String flag = Global.getValue("third_interface");
			for(int i= 0; i < size; i++){
				Borrow br = list.get(i);
				User user = cloanUserService.getById(br.getUserId());
				List<UserContacts> ucList = new ArrayList<UserContacts>();
				Channel channel = channelService.findById(br.getChannelId());
				br.setThirdAuth("1");
				if (channel != null && "kn".equals(channel.getCode())) { // 卡牛客户
					ucList = userContactsService.saveKnContacts(br.getUserId());
					//第三方接口开关：0是关闭
					if("0".equals(flag)){
						Map<String,Object>	 param = new HashMap<>();	
						param.put("userId", br.getUserId());
						List<BorrowRepay> listSelective = clBorrowRepayService.listSelective(param);
						if(CollectionUtils.isEmpty(listSelective)){
							br.setThirdAuth("0");	
							cafintechService.handleRefuse(br,"第三方开关关闭");
							continue;
						}
					}
				} else {
					ucList = userContactsService.findRelationsPhone(user.getId());
					//第三方接口开关：0是关闭
					if("0".equals(flag)){
						br.setThirdAuth("0");
						cafintechService.handleRefuse(br,"第三方开关关闭");
						continue;
					}
				}
				if(ucList.size() < 10){
					//通讯里小于10条直接机审拒绝、
					cafintechService.handleRefuse(br,"用户通讯录条数少于10");
				} else {
					cafintechService.caApply(br.getOrderNo(),br,ucList);
				}
			}
			succeed++;
			total++;
			if(size == 0){
				logger.info("未监测到待机审订单...");
			}else{
				logger.info("待机审订单审核完成...");
			}
		} catch (Exception e) {
			fail++;
			total++;
			logger.error(e.getMessage(),e);
		}
		
		quartzRemark = "执行总次数"+total+",成功"+succeed+"次,失败"+fail+"次";
		return quartzRemark;

	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		QuartzInfoService quartzInfoService = (QuartzInfoService) BeanUtil
				.getBean("quartzInfoService");
		QuartzLogService quartzLogService = (QuartzLogService) BeanUtil
				.getBean("quartzLogService");
		// 查询当前任务信息
		QuartzInfo quartzInfo = quartzInfoService.findByCode("doAutoAuditBorrow");
		Map<String, Object> qiData = new HashMap<>();
		qiData.put("id", quartzInfo.getId());

		QuartzLog quartzLog = new QuartzLog();
		quartzLog.setQuartzId(quartzInfo.getId());
		quartzLog.setStartTime(DateUtil.getNow());
		try {
			
			String remark = autoAuditBorrow();
			
			quartzLog.setTime(DateUtil.getNow().getTime() - quartzLog.getStartTime().getTime());
			quartzLog.setResult("10");
			quartzLog.setRemark(remark);
			qiData.put("succeed", quartzInfo.getSucceed() + 1);
		} catch (Exception e) {

			quartzLog.setResult("20");
			qiData.put("fail", quartzInfo.getFail() + 1);
			logger.error(e.getMessage(), e);

		} finally {
			logger.info("保存定时任务日志");

			quartzLogService.save(quartzLog);
			quartzInfoService.update(qiData);
		}

	}

}
