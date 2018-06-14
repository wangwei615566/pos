package com.rongdu.cashloan.manage.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.czwx.arbitrament.entity.ArbRespLog;
import com.czwx.arbitrament.model.ArbitramentModel;
import com.czwx.arbitrament.model.Result;
import com.czwx.arbitrament.model.ThirdParamsInfo;
import com.czwx.arbitrament.service.ArbitramentService;
import com.czwx.arbitrament.service.BorrowDataCollectService;
import com.czwx.arbitrament.util.ArbitramentException;
import com.czwx.arbitrament.util.HttpClientUtils;
import com.czwx.arbitrament.util.MD5;
import com.czwx.arbitrament.util.StringCompressUtils;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.common.web.controller.BaseController;
import com.rongdu.cashloan.core.domain.Borrow;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Controller
public class ArbController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ArbController.class);
    @Resource
    private ArbitramentService arbitramentService;
    @Resource
    private BorrowDataCollectService borrowDataCollectService;

    @RequestMapping(value = "/arb/queryBalance.htm")
    public void queryBalance() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap = arbitramentService.queryBalance();
        ServletUtils.writeToResponse(response, resultMap);
    }

    @RequestMapping(value = "/arb/caseSubmit.htm")
    public void caseSubmit(@RequestParam(value = "loanBillNos", required = true) String loanBillNos) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap = arbitramentService.caseSubmit(loanBillNos);
        ServletUtils.writeToResponse(response, resultMap);
    }

    @RequestMapping(value = "/arb/caseApplication.htm")
    public void caseApplication(@RequestParam(value = "loanBillNos", required = false) String loanBillNos,
                                @RequestParam(value = "batchNo", required = false) String batchNo
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap = arbitramentService.caseApplication(loanBillNos, batchNo);
        ServletUtils.writeToResponse(response, resultMap);
    }

    @RequestMapping(value = "/arb/caseCancel.htm")
    public void caseCancel(@RequestParam(value = "loanBillNo", required = true) String loanBillNo,
                           @RequestParam(value = "type", required = true) String type,
                           @RequestParam(value = "reason", required = true) String reason
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap = arbitramentService.caseCancel(loanBillNo, type, reason);
        ServletUtils.writeToResponse(response, resultMap);
    }

    @RequestMapping(value = "/arb/queryStatus.htm")
    public void queryStatus(@RequestParam(value = "loanBillNo", required = true) String loanBillNo
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap = arbitramentService.queryStatus(loanBillNo);
        ServletUtils.writeToResponse(response, resultMap);
    }

    @RequestMapping(value = "/arb/doDataCollect.htm")
    public void doDataCollect(@RequestParam(value = "loanBillNo", required = true) String loanBillNo
    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Borrow> borrowList = borrowDataCollectService.getBorrowList(loanBillNo);
        borrowDataCollectService.getClaimsTransfer(borrowList);
        borrowDataCollectService.getIdAddressAndNation(borrowList);
        borrowDataCollectService.getPaymentVoucher(borrowList);
        borrowDataCollectService.getReceipt(borrowList);
        ServletUtils.writeToResponse(response, resultMap);
    }
    /**
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/third.htm")
    public void third(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String exceptionMsg = "";
        String busiCode = "";
        String actualParamString = "";
        logger.debug("进入第三方对外接口");
        // 请求的参数
        ThirdParamsInfo paramsInfo = null;
        // 默认返回成功
        Result result = new Result(ArbitramentModel.RET_CODE_SUCC, ArbitramentModel.RET_MSG_SUCC);
        try {
            // 获取请求的参数
            String paramString = getParamString(request);
//            logger.info("paramString=" + paramString);
            if (paramString == null) {
                throw new ArbitramentException("1001", "参数不能为空");
            }
//            logger.info("paramString.length() = " + paramString.length());

            //需要通过解压来获取相应的json对象
            String deCompressString = StringCompressUtils.decompress(paramString);
            // 将参数转化成对象
            paramsInfo = JSON.parseObject(deCompressString, ThirdParamsInfo.class);
            // 参数验证
            if (this.validate(paramsInfo)) {
                // 签名
                String signCode = paramsInfo.getSignCode();
                // 通过参数本地生成签名
                String sign = MD5.md5(HttpClientUtils.generateSign(paramsInfo));
                logger.info("参数签名：" + signCode + ", 本地签名：" + sign);
                if (!signCode.equals(sign)) {
                    throw new ArbitramentException("1002", "参数【signCode】数据错误");
                }
                String param = paramsInfo.getParam();
                actualParamString = URLDecoder.decode(param, "UTF-8");
                if (StringUtils.isBlank(actualParamString)) {
                    throw new ArbitramentException("1002", "参数【param】数据错误");
                }
                // 商户编码
                String merchantCode = paramsInfo.getMerchantCode();
                if (!merchantCode.equals(ArbitramentModel.merchantCode)) {
                    throw new ArbitramentException("1002", "参数【merchantCode】数据错误");
                }
                // 业务编码
                busiCode = paramsInfo.getBusiCode();
                //以下开始处理各个不同的业务
                if (ArbitramentModel.BUSICODE_GETRESPONDENT.equalsIgnoreCase(busiCode)) {// 1.获取被申请人信息
                    // 被申请人信息查询
                    String jsonString = arbitramentService.getRespondent(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (ArbitramentModel.BUSICODE_GETCREDITAGREEMENT.equalsIgnoreCase(busiCode)) {// 2.获取借款协议
                    // 借款协议查询
                    String jsonString = arbitramentService.getCreditAgreement(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (ArbitramentModel.BUSICODE_GETCREDITINFO.equalsIgnoreCase(busiCode)) {// 3.获取借款信息
                    // 借款信息查询
                    String jsonString = arbitramentService.getCreditInfo(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (ArbitramentModel.BUSICODE_GETREFUNDINFO.equalsIgnoreCase(busiCode)) {// 4.获取还款信息
                    // 还款信息查询
                    String jsonString = arbitramentService.getRefundInfo(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (ArbitramentModel.BUSICODE_GETPAYVOUCHER.equalsIgnoreCase(busiCode)) {// 5.获取打款凭证
                    // 打款凭证查询
                    String jsonString = arbitramentService.getPayVoucher(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (ArbitramentModel.BUSICODE_GETPROMPTINFO.equalsIgnoreCase(busiCode)) {// 6.获取催款记录
                    // 催款记录查询
                    String jsonString = arbitramentService.getPrompt(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (ArbitramentModel.BUSICODE_GETFUNDINFO.equalsIgnoreCase(busiCode)) {// 7.获取案件订单相关金额
                    // 案件相关获取金额
                    String jsonString = arbitramentService.getFundInfo(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (ArbitramentModel.BUSICODE_GETORDERINFO.equalsIgnoreCase(busiCode)) {// 8.获取案件订单信息
                    // 获取案件订单信息
                    String jsonString = arbitramentService.getOrderInfo(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (ArbitramentModel.BUSICODE_SETSTATUS.equalsIgnoreCase(busiCode)) {// 9.案件订单状态通知
                    // TO_DO 需要自己处理此处业务
                    result = arbitramentService.setStatus(actualParamString);
                } else if (ArbitramentModel.BUSICODE_GETLITIGANTS.equalsIgnoreCase(busiCode)) {// 10.获取案件订单相关当事人信息
                    String jsonString = arbitramentService.getLitigants(actualParamString);
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else {
                    throw new ArbitramentException("1002", "参数【busiCode】数据错误");
                }
            }
        } catch (ArbitramentException e) {
            logger.error("第三方对外接口-Arbexpress异常", e);
            result = new Result(e.getErr_code(), e.getErr_msg());
            exceptionMsg = e.getErr_msg();
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
            logger.error("第三方对外接口-异常", e);
            result = new Result(ArbitramentModel.SYS_EXCEPTION_CODE, ArbitramentModel.SYS_EXCEPTION_MSG);
        } finally {
            // 返回的结果
            String result_msg = JSON.toJSONString(result);

            //保存请求日志记录
            ArbRespLog arbRespLog = new ArbRespLog();
            if(StringUtils.isNotBlank(actualParamString)){
                JSONObject jsonObject = JSON.parseObject(actualParamString);
                arbRespLog.setLoanBillNo(jsonObject.getString("loanBillNo"));
                arbRespLog.setReqParams(jsonObject.toJSONString());
            }

            arbRespLog.setBusiCode(busiCode);
            arbRespLog.setCreateTime(new Date());

            arbRespLog.setRespParams(result_msg);
            arbRespLog.setExceptionMsg(exceptionMsg);

            arbitramentService.saveRespLog(arbRespLog);

            // 返回结果
            returnMsg(response, StringCompressUtils.compress(result_msg));
        }
    }

    /**
     * 从请求流中获取参数json串
     *
     * @param request
     * @return
     * @throws IOException
     */
    public String getParamString(HttpServletRequest request) throws IOException {
        final int BUFFER_SIZE = 8 * 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream sis = request.getInputStream();
        int length = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((length = sis.read(buffer)) > 0) {
            baos.write(buffer, 0, length);
        }
        String bodyData = new String(baos.toByteArray(), "UTF-8");
        return bodyData;
    }

    /**
     * 接口参数验证
     *
     * @param thirdParamsInfo
     * @return
     * @throws ArbitramentException
     */
    public boolean validate(ThirdParamsInfo thirdParamsInfo) throws ArbitramentException {
        String merchantCode = thirdParamsInfo.getMerchantCode();
        if (StringUtils.isBlank(merchantCode)) {
            throw new ArbitramentException("1001", "参数不能为空");
        }
        String format = thirdParamsInfo.getFormat();
        if (StringUtils.isBlank(format)) {
            thirdParamsInfo.setFormat("JSON");
        }
        if (!"JSON".equalsIgnoreCase(format)) {
            throw new ArbitramentException("1002", "参数【format】数据错误");
        }
        String encode = thirdParamsInfo.getEncode();
        if (StringUtils.isBlank(encode)) {
            thirdParamsInfo.setEncode("UTF-8");
        }
        if (!"UTF-8".equalsIgnoreCase(encode)) {
            throw new ArbitramentException("1002", "参数【encode】数据错误");
        }
        String busiCode = thirdParamsInfo.getBusiCode();
        if (StringUtils.isBlank(busiCode)) {
            throw new ArbitramentException("1001", "参数不能为空");
        }
        String param = thirdParamsInfo.getParam();
        if (StringUtils.isBlank(param)) {
            throw new ArbitramentException("1001", "参数不能为空");
        }
        String time = thirdParamsInfo.getTime();
        if (StringUtils.isBlank(time)) {
            throw new ArbitramentException("1001", "参数不能为空");
        }
        String signType = thirdParamsInfo.getSignType();
        if (StringUtils.isBlank(signType)) {
            thirdParamsInfo.setSignType("MD5");
        }
        if (!"MD5".equalsIgnoreCase(signType)) {
            throw new ArbitramentException("1002", "参数【signType】数据错误");
        }
        String signCode = thirdParamsInfo.getSignCode();
        if (StringUtils.isBlank(signCode)) {
            throw new ArbitramentException("1001", "参数【signCode】不能为空");
        }
        return true;
    }

    /**
     * 返回的信息
     *
     * @param response
     * @param msg
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private void returnMsg(HttpServletResponse response, String msg) throws UnsupportedEncodingException, IOException {
        response.getOutputStream().write(msg.getBytes("utf-8"));
    }
}
