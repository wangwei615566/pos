package com.rongdu.cashloan.manage.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.domain.Channel;
import com.rongdu.cashloan.cl.model.BorrowRepayModel;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ChannelService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.UserContactsService;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;

import tool.util.BeanUtil;
import tool.util.DateUtil;


@Component
@Lazy(value = false)
public class QuartzSyncKnUserContact implements Job{
	
	private static final Logger logger = Logger.getLogger(QuartzSyncKnUserContact.class);
	/**
	 * 定时计算逾期
	 * @throws ServiceException 
	 */
	public String syncKnUserContact() throws ServiceException {
		BorrowRepayService borrowRepayService = (BorrowRepayService)BeanUtil.getBean("borrowRepayService");
		ClBorrowService clBorrowService = (ClBorrowService)BeanUtil.getBean("clBorrowService");
		ChannelService channelService = (ChannelService) BeanUtil.getBean("channelService");
		UserContactsService userContactsService = (UserContactsService) BeanUtil.getBean("clUserContactsService");
		String quartzRemark = null;
		int succeed = 0;
		int fail = 0;
		int total = 0;
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("state",BorrowRepayModel.STATE_REPAY_NO);
		List<BorrowRepay> list = borrowRepayService.listSelective(paramMap);
		if (!list.isEmpty()) {
			try {
				for (int i = 0; i < list.size(); i++) {
					Borrow borrow = clBorrowService.findByPrimary(list.get(i).getBorrowId());
					//查询渠道
					Channel channel = channelService.findById(borrow.getChannelId());
					if(channel != null && "kn".equals(channel.getCode())){
						//逾期重新获取通讯录
						userContactsService.saveKnContacts(borrow.getUserId());
					}
				}
			succeed++;
			total++;
		} catch (Exception e) {
			fail ++;
			total++;
			logger.error(e.getMessage(),e);
		}	
		
	}
		logger.info("逾期计算结束...");
		quartzRemark = "执行总次数"+total+",成功"+succeed+"次,失败"+fail+"次";
		return quartzRemark;
	}
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		QuartzInfoService quartzInfoService = (QuartzInfoService)BeanUtil.getBean("quartzInfoService");
		QuartzLogService quartzLogService = (QuartzLogService)BeanUtil.getBean("quartzLogService");
		QuartzLog ql = new QuartzLog();
		Map<String,Object> qiData = new HashMap<>();
		QuartzInfo qi = quartzInfoService.findByCode("syncKnUserContact");
		try {
			qiData.put("id", qi.getId());
			ql.setQuartzId(qi.getId());
			ql.setStartTime(DateUtil.getNow());
			
			String remark = syncKnUserContact();
			
			ql.setTime(DateUtil.getNow().getTime()-ql.getStartTime().getTime());
			ql.setResult("10");
			ql.setRemark(remark);
			qiData.put("succeed", qi.getSucceed()+1);
			
		}catch (Exception e) {
			ql.setResult("20");
			qiData.put("fail", qi.getFail()+1);
			logger.error(e.getMessage(),e);
		}finally{
			logger.info("保存定时任务日志");
			quartzLogService.save(ql);
			quartzInfoService.update(qiData);
		}
	}
}
