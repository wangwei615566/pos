package com.rongdu.cashloan.manage.job;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.cajl.approve.sdk.util.JsonUtil;
import com.czwx.cafintech.entity.DataProdDO;
import com.czwx.cafintech.entity.DataProdVO;
import com.czwx.cafintech.mapper.DataProdDOMapper;
import com.rongdu.cashloan.cl.domain.BorrowProgress;
import com.rongdu.cashloan.cl.domain.Channel;
import com.rongdu.cashloan.cl.mapper.LoanEntranceMapper;
import com.rongdu.cashloan.cl.service.BorrowProgressService;
import com.rongdu.cashloan.cl.service.BorrowRepayService;
import com.rongdu.cashloan.cl.service.ChannelService;
import com.rongdu.cashloan.cl.service.ClBorrowService;
import com.rongdu.cashloan.cl.service.SyncLoanInfoKnService;
import com.rongdu.cashloan.cl.service.impl.SyncLoanInfoKnServiceImpl;
import com.rongdu.cashloan.core.common.exception.ServiceException;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;

import tool.util.BeanUtil;
import tool.util.DateUtil;

@Component
@Lazy(value = false)
public class QuartzNotifyBorrowToKn implements Job{
	
	private static final Logger logger = Logger.getLogger(QuartzNotifyBorrowToKn.class);

	/**
	 * 延迟通知卡牛机审不通过订单
	 * 
	 * @throws ServiceException
	 */
	public String notifyKn() throws ServiceException {

		final BorrowProgressService borrowProgressService = (BorrowProgressService) BeanUtil.getBean("borrowProgressService");
		final LoanEntranceMapper loanEntranceMapper = (LoanEntranceMapper) BeanUtil.getBean("loanEntranceMapper");
		final SyncLoanInfoKnService syncLoanInfoKnService = new SyncLoanInfoKnServiceImpl();
		ChannelService channelService =(ChannelService) BeanUtil.getBean("channelService");
		DataProdDOMapper dataProdDOMapper = (DataProdDOMapper) BeanUtil.getBean("dataProdDOMapper");
		ClBorrowService clBorrowService = (ClBorrowService) BeanUtil.getBean("clBorrowService");
		logger.info("进入检查卡牛机审拒绝订单...");
		String quartzRemark = null;
		int succeed = 0;
		int fail = 0;
		int total = 0;
		try {
			Map<String,Object> searchMap = new HashMap<String,Object>();
			Channel channel = channelService.findByCode("kn");
			 if(channel != null){
				 searchMap.put("channelId", channel.getId());
			 }else{
				 searchMap.put("channelId", -1);
			 }
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -5);
			searchMap.put("curTime",calendar.getTime());
			 List<BorrowProgress> list = borrowProgressService.findProgressNotifyKn(searchMap);
			int size = list.size();
			for(int i= 0; i < size; i++){
				 BorrowProgress bp = list.get(i);
				 Borrow borrow = clBorrowService.findByPrimary(bp.getBorrowId());
				 DataProdDO dataProdDO = dataProdDOMapper.selectByNoBusB(borrow.getOrderNo());
				 Map<String, Object> pMap = new HashMap<String, Object>();
				 pMap.put("loanId", String.valueOf(bp.getBorrowId()));// 借款id
	    			 pMap.put("loanStatus","4");//借款状态
	    			 pMap.put("auditTime",bp.getCreateTime().getTime());//审批时间
	    			 pMap.put("refusedReason",dataProdDO == null ? "机审被拒未知原因" : dataProdDO.getReason());//拒绝原因
				logger.info("通知参数"+JsonUtil.toJson(pMap));
				syncLoanInfoKnService.syncLoanInfoKn(pMap,false,"机审不通过通知卡牛");
	    			bp.setKnJobNotify("1");
	    			borrowProgressService.updateById(bp);
				 
			}
			succeed++;
			total++;
		} catch (Exception e) {
			fail++;
			total++;
			logger.error(e.getMessage(),e);
		}
		logger.info("检查卡牛机审拒绝订单完成...");
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
		QuartzInfo quartzInfo = quartzInfoService.findByCode("doNotifyKn");
		Map<String, Object> qiData = new HashMap<>();
		qiData.put("id", quartzInfo.getId());

		QuartzLog quartzLog = new QuartzLog();
		quartzLog.setQuartzId(quartzInfo.getId());
		quartzLog.setStartTime(DateUtil.getNow());
		try {
			
			String remark = notifyKn();
			
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
