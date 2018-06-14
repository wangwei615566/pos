package com.rongdu.cashloan.manage.service.impl;

import com.rongdu.cashloan.cl.mapper.BorrowRepayMapper;
import com.rongdu.cashloan.cl.mapper.ClBorrowMapper;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.domain.Borrow;
import com.rongdu.cashloan.core.model.BorrowModel;
import com.rongdu.cashloan.manage.service.SharingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("sharingService")
public class SharingServiceImpl implements SharingService {
    @Resource
    private ClBorrowMapper clBorrowMapper;
    @Resource
    private BorrowRepayMapper borrowRepayMapper;

    @Override
    public Map<String, Object> getContract(String orderNo, String customerName, String paperNumber) {
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("orderNo", orderNo);
        queryMap.put("realName", customerName);
        queryMap.put("idNo", paperNumber);
        Borrow borrow = clBorrowMapper.queryByOrderAndUser(queryMap);
        if (borrow != null) {
            data.put("customerName", customerName);
            data.put("paperNumber", paperNumber);
            data.put("loanId", orderNo);
            data.put("loanTypeDesc", "扩大经营");
            String[] noPass = {BorrowModel.STATE_PRE, BorrowModel.STATE_AUDIT_PASS, BorrowModel.STATE_AUDIT_REFUSED, BorrowModel.STATE_AUTO_PASS, BorrowModel.STATE_AUTO_REFUSED, BorrowModel.STATE_NEED_REVIEW, BorrowModel.STATE_REFUSED};
            if (Arrays.asList(noPass).contains(borrow.getState())) {
                //01 审批通过；02 审批拒绝；05 客户取消
                data.put("checkResult", "02");
            } else {
                data.put("checkResult", "01");
            }
            data.put("checkResultTime", DateUtil.dateStr2(borrow.getCreateTime()));
            data.put("loanMoney", borrow.getAmount());
            //担保类型 A 抵押；B 质押；C 担保；D 信用；E 保证；Y 其他
            data.put("loanAssureType", "D");
            data.put("loanStartDate", DateUtil.dateStr2(borrow.getCreateTime()));
            data.put("loanEndDate", DateUtil.dateStr2(DateUtil.rollDay(borrow.getCreateTime(), Integer.parseInt(borrow.getTimeLimit()) - 1)));
            //还款期数,单位：月
            data.put("loanPeriods", "1");
            data.put("loanCreditCity", paperNumber.substring(0, 6));
            res.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
            res.put(Constant.RESPONSE_DATA, data);
        }else {
            res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
        }

        return res;
    }

    @Override
    public Map<String, Object> getNewOverdue() {
        Map<String, Object> res = new HashMap<>();
//        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> mapList = borrowRepayMapper.findIncreaseOverdue();
       /* data.put("customerName","");
        data.put("paperNumber","");
        data.put("loanId","");
        data.put("loanTypeDesc","");
        data.put("overdueStartDate","");
        data.put("nbMoney","");
        data.put("loanMoney","");
        data.put("loanAssureType","");
        data.put("loanStartDate","");
        data.put("loanEndDate","");
        data.put("loanPeriods","");
        data.put("loanCreditCity","");*/
        res.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
        res.put(Constant.RESPONSE_DATA, mapList);
        return res;
    }

    @Override
    public Map<String, Object> getStillOverdue(String orderNo, String customerName, String paperNumber) {
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("orderNo", orderNo);
        queryMap.put("realName", customerName);
        queryMap.put("idNo", paperNumber);
        res = borrowRepayMapper.findOverdueByOrderNoAndUser(queryMap);
        if (res != null) {
            data.put("customerName", customerName);
            data.put("paperNumber", paperNumber);
            data.put("loanId", orderNo);
            data.put("loanTypeDesc", "商场消费");
            data.put("stillOverdue", res.get("stillOverdue"));
            data.put("nbMoney", res.get("nbMoney"));
            res.clear();
            res.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
            res.put(Constant.RESPONSE_DATA, data);
        } else {
            res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
        }
        return res;
    }

    @Override
    public Map<String, Object> getFinish(String orderNo, String customerName, String paperNumber) {
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("orderNo", orderNo);
        queryMap.put("realName", customerName);
        queryMap.put("idNo", paperNumber);
        String overdueFinish = borrowRepayMapper.overdueFinishByOrderNoAndUser(queryMap);
        if (StringUtils.isNotBlank(overdueFinish)) {
            data.put("customerName", customerName);
            data.put("paperNumber", paperNumber);
            data.put("loanId", orderNo);
            data.put("loanTypeDesc", "商场消费");
            data.put("overdueFinish", overdueFinish);
            res.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
            res.put(Constant.RESPONSE_DATA, data);
        } else {
            res.put(Constant.RESPONSE_CODE, Constant.FAIL_CODE_VALUE);
        }


        return res;
    }
}
