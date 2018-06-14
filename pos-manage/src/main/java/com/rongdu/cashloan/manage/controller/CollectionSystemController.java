package com.rongdu.cashloan.manage.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.rongdu.cashloan.cl.model.FileTypeUtil;
import com.rongdu.cashloan.cl.model.UploadFileRes;
import com.rongdu.cashloan.cl.service.CollectionSystemService;
import com.rongdu.cashloan.core.common.context.Constant;
import com.rongdu.cashloan.core.common.context.Global;
import com.rongdu.cashloan.core.common.util.DateUtil;
import com.rongdu.cashloan.core.common.util.JsonUtil;
import com.rongdu.cashloan.core.common.util.MapUtil;
import com.rongdu.cashloan.core.common.util.ServletUtils;
import com.rongdu.cashloan.core.common.util.StringUtil;
import com.rongdu.cashloan.core.common.web.controller.BaseController;
import com.wangyin.npp.util.MD5;

import tool.util.BeanUtil;

/**
 * 催收系统接口
 * Created by wang.wei on 2017/11/7.
 */
@Scope("prototype")
@Controller
public class CollectionSystemController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CollectionSystemController.class);
    @RequestMapping(value ="/api/collection/{serviceName}",method=RequestMethod.POST)
    public void main(HttpServletRequest request, HttpServletResponse response, @PathVariable String serviceName){
        logger.debug("===================>calling interface ["+serviceName+"]<=================================================");
        //创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String,Object> noFileMap = new LinkedHashMap<String, Object>();
        //判断 request 是否有文件上传,即多部分请求
        if(multipartResolver.isMultipart(request)){
            //转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            Iterator<String> iterFileNames = multiRequest.getFileNames();
            while (iterFileNames.hasNext()) {
                String fileParamName = iterFileNames.next();
                //取得上传文件
                MultipartFile file = multiRequest.getFile(fileParamName);
                if(file != null){
                    //取得当前上传文件的文件名称
                    String myFileName = file.getOriginalFilename();
                    //如果名称不为“”,说明该文件存在，否则说明该文件不存在
                    if(myFileName.trim() !=""){
                        try {
                        	UploadFileRes model = save(file);
                        	if(StringUtils.isNotBlank(model.getErrorMsg())){
                        		Map<String,Object> result = new HashMap<String,Object>();
                        		result.put(Constant.RESPONSE_RETCODE, Constant.FAIL_KNCODE_PARAM);
                    			result.put(Constant.RESPONSE_RETMSG, model.getErrorMsg());
                    			ServletUtils.writeNoFormatDateToResponse(response,result);
                        		return;
                        	}
                            String path = model.getResPath();
                            if(path != null){
                                //map.put(fileParamName, Global.getValue("server_host") + "/readFile.htm?path=" + URLEncoder.encode(path,"UTF-8"));
                            	map.put(fileParamName, path);
                            }
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                            Map<String,Object> result = new HashMap<String,Object>();
                    		result.put(Constant.RESPONSE_RETCODE, Constant.FAIL_KNCODE_EXP);
                			result.put(Constant.RESPONSE_RETMSG, "系统异常");
                			ServletUtils.writeNoFormatDateToResponse(response,result);
                    		return;
                        }

                    }
                }

            }
            Enumeration<String> iterParamNames = multiRequest.getParameterNames();
            String parametername = null;
            while(iterParamNames.hasMoreElements()){
                parametername = iterParamNames.nextElement().toString();
                map.put(parametername, multiRequest.getParameter(parametername).trim());
                noFileMap.put(parametername, multiRequest.getParameter(parametername));
            }
        }else {
            Enumeration<String> parameterNames = request.getParameterNames();
            String parametername = null;
            while(parameterNames.hasMoreElements()){
                parametername = parameterNames.nextElement().toString();
                map.put(parametername, request.getParameter(parametername).trim());
                noFileMap.put(parametername, request.getParameter(parametername));
            }
        }
        logger.info("请求参数："+JsonUtil.toString(map));
        //验证签名
        try {
	        		verifySign(noFileMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //service层业务
        if(map.get("ip") == null){
        		map.put("ip", getRealIp());
        }
        try{
        	Map<String,Object> result = ((CollectionSystemService) BeanUtil.getBean(serviceName)).execute(map);
            ServletUtils.writeNoFormatDateToResponse(response,result);
        }catch(Exception e){
        	logger.error(e.getMessage(), e);
        	Map<String,Object> result = new HashMap<String,Object>();
        	result.put(Constant.RESPONSE_RETCODE, Constant.FAIL_KNCODE_EXP);
        	result.put(Constant.RESPONSE_RETMSG, "系统异常");
        	ServletUtils.writeNoFormatDateToResponse(response,result);
        }
    }
    /**
     * 保存上传文件
     * @param file
     * @return
     */
    private UploadFileRes save(MultipartFile file) {
        UploadFileRes model = new UploadFileRes();
        model.setCreateTime(DateUtil.getNow());

        // 文件名称
        String picName = file.getOriginalFilename();
        logger.info("图片名称----------------"+picName);
        CommonsMultipartFile cf = (CommonsMultipartFile) file;
        DiskFileItem fi = (DiskFileItem) cf.getFileItem();
        File newFile = fi.getStoreLocation();
        if(!newFile.exists()){
        	try {
				newFile.createNewFile();
				byte[] b = cf.getBytes();
				OutputStream out = new FileOutputStream(newFile);
				out.write(b, 0, b.length);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        logger.info("图片----------"+newFile);
        // 文件格式
        String fileType = FileTypeUtil.getFileType(newFile);
        if (StringUtil.isBlank(fileType) || !FileTypeUtil.isImage(newFile, fileType)) {
            model.setErrorMsg("图片格式错误或内容不规范");
            newFile.delete();
            return model;
        }
        // 校验图片大小
        Long picSize = file.getSize();
        if (picSize.compareTo(20971520L) > 0) {
            model.setErrorMsg("文件超出20M大小限制");
            return model;
        }
        // 保存文件
        String s=File.separator;
        String dataDir = Global.getValue("kn_file_home");
        
        String filePath = "";
        if(s.equals("\\")){  // windows
            filePath = "D:"+filePath;
        } else {
        	filePath = FilenameUtils.getFullPath(dataDir)+"data"+s+"image"+s + fileType + s + System.currentTimeMillis() + s + picName;
        }
        File files = new File(filePath);
        if (!files.exists()) {
            try {
                files.mkdirs();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                model.setErrorMsg("文件目录不存在");
                return model;
            }
        }
        try {
            file.transferTo(files);
        } catch (IllegalStateException | IOException e) {
            logger.error(e.getMessage(), e);
        }
        // 转存文件
        model.setResPath(filePath);
        model.setFileName(picName);
        model.setFileFormat(fileType);
        model.setFileSize(new BigDecimal(picSize));
        newFile.delete();
        return model;
    }
    private void verifySign(Map<String,Object> params) throws Exception{
		String sign = request.getParameter("sign");
		params.remove("sign");
		Map<String,Object> result = new HashMap<String,Object>();
		if(StringUtils.isBlank(sign)){
			result.put(Constant.RESPONSE_RETCODE, Constant.FAIL_KNCODE_SIGN);
			result.put(Constant.RESPONSE_RETMSG, "没有sign");
			ServletUtils.writeNoFormatDateToResponse(response,result);
			return;
		}
		String verfiySign = MD5.md5(doSign(params), "Czwx@2017_123");
		if(!verfiySign.equals(sign)){
			result.put(Constant.RESPONSE_RETCODE, Constant.FAIL_KNCODE_SIGN);
			result.put(Constant.RESPONSE_RETMSG, "签名错误");
			ServletUtils.writeNoFormatDateToResponse(response,result);
			return;
		}
	}
	
	private String doSign(Map<String,Object> params){
		Map<String, Object> map = MapUtil.simpleSort(params);
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();
			sb.append(name + "=" + value).append("&");
		}

		if (sb.length() > 1)
			sb.deleteCharAt(sb.length() - 1);
		logger.debug("签名验签" + sb.toString());
		return sb.toString();
	}
}

