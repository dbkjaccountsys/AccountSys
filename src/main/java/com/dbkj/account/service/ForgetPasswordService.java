package com.dbkj.account.service;

import javax.servlet.http.HttpSession;

import com.dbkj.account.dto.Result;
import com.dbkj.account.util.RandomUtil;
import com.dbkj.account.util.ValidateUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

public class ForgetPasswordService {
	
	public static final String SMS_CODE="smsCodeKey";
	
	public static final String IS_VALIATE_MOBILE="isValidateMobile";
	
	private SmsService smsService=new SmsService();

	
	public Result<?> getCode(String mobile,String code,Controller controller){
		Result<?> result=new Result<>();
		if(StrKit.isBlank(mobile)||!ValidateUtil.validateMobilePhone(mobile)){
			result.setSuccess(false);
			result.setReason("请输入正确的手机号码");
			return result;
		}
		if(StrKit.isBlank(code)||!code.equals(controller.getSessionAttr(VertifyCodeService.VERTIFY_CODE).toString())){
			result.setSuccess(false);
			result.setReason("验证码错误");
			return result;
		}
		//设置session有效期为5分钟
		HttpSession session=controller.getSession();
		session.setMaxInactiveInterval(5*60);
		String smsCode=RandomUtil.getRanCode(6);
		session.setAttribute(ForgetPasswordService.SMS_CODE, smsCode);
		smsService.sendSms(mobile, smsCode);
		result.setSuccess(true);
		return result;
	}
	
	
//	public boolean isExistPhone(String phone){
//		
//	}
}
