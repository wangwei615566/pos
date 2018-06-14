package com.rongdu.cashloan.manage.job;

import com.rongdu.cashloan.cl.domain.BorrowRepay;
import com.rongdu.cashloan.cl.domain.Channel;
import com.rongdu.cashloan.cl.model.BorrowRepayModel;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ChannelService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.ClSmsService;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.domain.User;
import com.rongdu.cashloan.core.service.CloanUserService;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tool.util.BeanUtil;
import tool.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Lazy(value = false)
public class QuartzUrgeSms implements Job{
	
	private static final Logger logger = Logger.getLogger(QuartzUrgeSms.class);
	
	/**
	 * 催收短信
	 * @throws ServiceException 
	 */
	public String urgeSms() throws ServiceException {
		BorrowRepayService borrowRepayService = (BorrowRepayService)BeanUtil.getBean("borrowRepayService");
		ClBorrowService clBorrowService = (ClBorrowService)BeanUtil.getBean("clBorrowService");
		ClSmsService clSmsService = (ClSmsService) BeanUtil.getBean("clSmsService");
		CloanUserService userSerivce = (CloanUserService)BeanUtil.getBean("cloanUserService");
		ChannelService channelService = (ChannelService)BeanUtil.getBean("channelService");
		logger.info("进入发送提前还款短信...");
		String quartzRemark = null;
		int succeed = 0;
		int fail = 0;
		int total = 0;
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("state",BorrowRepayModel.STATE_REPAY_NO);
		List<BorrowRepay> list = borrowRepayService.listSelective(paramMap);
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
                try {
                    Borrow borrow = clBorrowService.findByPrimary(list.get(i).getBorrowId());
                    Channel channel = channelService.findById(borrow.getChannelId());
                    String platform = "成长钱包";
                    if (channel != null) {
                        platform = channel.getName();
                    }
                    long day = DateUtil.daysBetween(new Date(), list.get(i).getRepayTime());//计算还款日期和当前日期相差天数
                   	int limitDay = 3;
                    if("28".equals(borrow.getTimeLimit()) && day == 5) {
						limitDay = 5;
						day = 5;
					}
                    User user = userSerivce.getById(borrow.getUserId());

                    Map<String,Object> data = new HashMap<String,Object>();
					data.put("platform", platform);
					data.put("loan",borrow.getAmount());
                    if ("prod".equals(Global.getValue("app_environment")) || "pre".equals(Global.getValue("app_environment")) ) {
                        if (day <= limitDay && day > 0) {
                            logger.info("开始发送还款提醒短信...");
                            //TODO  发送还款提醒短息
							data.put("time", DateUtil.dateStr6(list.get(i).getRepayTime()));
							data.put("leftDay", day);
							clSmsService.sendSms("advanceFiveDayInform", data, user.getLoginName());
                        } else if (day == 0) {
                            //尊敬的用户，您在{$platform}借款{$loan}元，今天为最后还款期限日，请按时归还!
							clSmsService.sendSms("lastDayInform", data, user.getLoginName());
                        }
                    }
                    succeed++;
                    total++;
                } catch (Exception e) {
                    fail++;
                    total++;
                    logger.error(e.getMessage(), e);
                }
            }
		}
		logger.info("发送提前还款短信结束...");
		quartzRemark = "执行总次数"+total+",成功"+succeed+"次,失败"+fail+"次";
		return quartzRemark;
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		QuartzInfoService quartzInfoService = (QuartzInfoService)BeanUtil.getBean("quartzInfoService");
		QuartzLogService quartzLogService = (QuartzLogService)BeanUtil.getBean("quartzLogService");
		QuartzLog ql = new QuartzLog();
		Map<String,Object> qiData = new HashMap<>();
		QuartzInfo qi = quartzInfoService.findByCode("doUrgeSms");
		try {
			qiData.put("id", qi.getId());
			ql.setQuartzId(qi.getId());
			ql.setStartTime(DateUtil.getNow());
			
			String remark = urgeSms();
			
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
	public static void main(String[] args) throws ParseException{
		long day = DateUtil.daysBetween(new Date(), new SimpleDateFormat("yyyy-MM-dd").parse("2017-07-29"));
		System.out.println(day);
	}
}
