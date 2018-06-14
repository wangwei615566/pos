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
import org.springframework.util.CollectionUtils;

import com.rongdu.cashloan.cl.domain.RcDataStatistics;
import com.rongdu.cashloan.cl.service.RcDataStatisticsService;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;

import tool.util.BeanUtil;
import tool.util.DateUtil;

@Component
@Lazy(value = false)
public class QuartzRcDataStatistics implements Job{
	
	
	
	private static final Logger logger = Logger.getLogger(QuartzRcDataStatistics.class);

	/**
	 * 定时统计借款信息任务
	 * @throws ServiceException
	 */
	public void count() throws ServiceException {
		int msg = 0;
		RcDataStatisticsService rcDataStatisticsService = (RcDataStatisticsService)BeanUtil.getBean("rcDataStatisticsService");
		QuartzInfoService quartzInfoService = (QuartzInfoService)BeanUtil.getBean("quartzInfoService");
		QuartzLogService quartzLogService = (QuartzLogService)BeanUtil.getBean("quartzLogService");
			
		QuartzInfo qi = quartzInfoService.findByCode("doRcDataStatistics");
		Map<String,Object> qiData = new HashMap<>();
		qiData.put("id", qi.getId());

		QuartzLog ql = new QuartzLog();
		ql.setQuartzId(qi.getId());
		ql.setStartTime(DateUtil.getNow());
		
		
		try {
			logger.info("进入风控数据统计...");
			Map<String,Object> searchMap = new HashMap<>();
			//分渠道进行统计
			List<RcDataStatistics> dateChannelStatistics = rcDataStatisticsService.dateChannelStatistics(searchMap);
			//查询所有渠道总计
			RcDataStatistics dateAllStatistics = rcDataStatisticsService.dateAllStatistics(searchMap);
			if(!CollectionUtils.isEmpty(dateChannelStatistics)){
				for(RcDataStatistics rcDataStatistics : dateChannelStatistics){
					msg = rcDataStatisticsService.save(rcDataStatistics);
				}
				if(dateAllStatistics != null){
					dateAllStatistics.setChannelId(0L);
					msg = rcDataStatisticsService.save(dateAllStatistics);
				}
			}
			if (dateAllStatistics == null || CollectionUtils.isEmpty(dateChannelStatistics) || msg == 0) {
				ql.setTime(DateUtil.getNow().getTime()-ql.getStartTime().getTime());
				ql.setResult("20");
				ql.setRemark("执行失败");
				qiData.put("fail", qi.getFail()+1);
				logger.error("定时统计任务查询失败");
			}else {
				ql.setTime(DateUtil.getNow().getTime()-ql.getStartTime().getTime());
				ql.setResult("10");
				ql.setRemark("执行成功");
				qiData.put("succeed", qi.getSucceed()+1);
			}		
			logger.info("统计结束...");
		} catch (Exception e) {
			ql.setTime(DateUtil.getNow().getTime()-ql.getStartTime().getTime());
			ql.setResult("20");
			ql.setRemark("执行失败");
			qiData.put("fail", qi.getFail()+1);
			logger.error(e.getMessage(),e);
		}finally{
			quartzLogService.save(ql);
			quartzInfoService.update(qiData);
		}
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
			try {
				count();
			} catch (ServiceException e) {
				logger.error(e.getMessage(),e);
			}
		
		
	}

}
