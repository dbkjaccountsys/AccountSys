package com.dbkj.account.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.util.DateUtil;
import com.dbkj.account.util.FileUtil;
import com.dbkj.account.util.ValidateUtil;
import com.dbkj.account.util.VerificationCodeTool;
import com.dbkj.account.util.VerificationCodeTool.RandomResult;
import com.jfinal.core.Controller;

public class VertifyCodeService {

	
private static final Logger logger=LoggerFactory.getLogger(ManageLoginService.class);
	
	public static final String VERTIFY_CODE="vertify_code";
	
	/**
	 * 生成验证�?
	 * @param controller
	 * @return
	 */
	public File getVertifyCode(Controller controller){
		//获取项目根路径的绝对路径
		String path=controller.getRequest().getServletContext().getRealPath("/");
		if(logger.isDebugEnabled()){
			logger.debug("项目根路径：{}",path);
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Date today=new Date();
		//删除昨天的验证码图片
		Date yesterday=DateUtil.addDays(today, -1);
		String yesterdayStr=sdf.format(yesterday);
		try{
			FileUtil.deleteDir(path+"/images/code/"+yesterdayStr);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		
		String dateStr=sdf.format(today);
		File dir=new File(path+"/images/code/"+dateStr);
		if(!dir.exists()){
			dir.mkdirs();
		}
		//生成验证码和验证码图�?
		File file=new File(dir,UUID.randomUUID().toString()+".jpg");
		RandomResult result=VerificationCodeTool.randomString();
		//设置session的过期时间为5分钟
		controller.setSessionAttr(VertifyCodeService.VERTIFY_CODE, result.getResult());
		VerificationCodeTool.outputImage(65, 30, file, result.getRandomString());
		return file;
	}
	
	public boolean validate(String code,Controller controller){
		if(ValidateUtil.isNumeric(code)){
			Integer result=controller.getSessionAttr(VertifyCodeService.VERTIFY_CODE);
			return result!=null&&result.intValue()==Integer.parseInt(code);
		}
		return false;
	}
}
