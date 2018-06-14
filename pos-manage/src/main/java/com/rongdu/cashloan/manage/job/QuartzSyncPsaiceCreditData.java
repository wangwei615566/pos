package com.rongdu.cashloan.manage.job;

import com.aliyun.oss.model.PutObjectResult;
import com.rongdu.cashloan.manage.domain.QuartzInfo;
import com.rongdu.cashloan.manage.domain.QuartzLog;
import com.rongdu.cashloan.manage.service.QuartzInfoService;
import com.rongdu.cashloan.manage.service.QuartzLogService;
import com.rongdu.cashloan.manage.service.QuartzPsaiceCreditSyncService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tool.util.BeanUtil;
import tool.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

@Component
@Lazy(value = false)
public class QuartzSyncPsaiceCreditData implements Job {

    private static final Logger logger = LoggerFactory.getLogger(QuartzSyncPsaiceCreditData.class);

    private static volatile boolean isRun = false;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        logger.info("========run 上传贷后数据===");
        if(!QuartzSyncPsaiceCreditData.isRun) {
            QuartzSyncPsaiceCreditData.isRun = true;

            long startTime = System.currentTimeMillis();

            logger.info("========start 上传贷后数据===");

            //log
            QuartzInfoService quartzInfoService = (QuartzInfoService) BeanUtil
                    .getBean("quartzInfoService");
            QuartzLogService quartzLogService = (QuartzLogService) BeanUtil
                    .getBean("quartzLogService");

            // 查询当前任务信息
            QuartzInfo quartzInfo = quartzInfoService.findByCode("doRepayUpload");
            Map<String, Object> qiData = new HashMap<>();
            qiData.put("id", quartzInfo.getId());
            qiData.put("fail", quartzInfo.getFail());

            QuartzLog quartzLog = new QuartzLog();
            quartzLog.setQuartzId(quartzInfo.getId());
            quartzLog.setStartTime(DateUtil.getNow());


            QuartzPsaiceCreditSyncService syncDataService = (QuartzPsaiceCreditSyncService) BeanUtil.getBean("quartzPsaiceCreditSyncService");
            PutObjectResult ret = null;
            try {

                ret = syncDataService.uploadPlanTask();


                quartzLog.setTime(DateUtil.getNow().getTime() - quartzLog.getStartTime().getTime());
                quartzLog.setResult("10");
                quartzLog.setRemark(ret.getETag());
                qiData.put("succeed", quartzInfo.getSucceed() + 1);

            } catch (Exception e) {
                logger.error("=====上传贷后数据 任务失败", e);
                quartzLog.setResult("20");
                quartzLog.setRemark("");
                qiData.put("fail", quartzInfo.getFail() + 1);
                logger.error(e.getMessage(), e);

            }finally {

                logger.info("上传贷后数据 保存定时任务日志");

                quartzLogService.save(quartzLog);
                if(qiData.size() > 1){
                    quartzInfoService.update(qiData);
                }

                QuartzSyncPsaiceCreditData.isRun = false;
            }

            long limitTime = System.currentTimeMillis() - startTime;
            logger.info("=start task end==the result is = {}  use time ={}",ret, limitTime);

        }
    }

}