package com.dbkj.account.validator;

import com.dbkj.account.service.ForgetPasswordService;
import com.dbkj.account.service.VertifyCodeService;
import com.dbkj.account.util.ValidateUtil;
import com.dbkj.account.vo.ForgetPasswordVo;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

public class ForgetPasswordValidator extends Validator{

	@Override
	protected void validate(Controller c) {
		Res res=I18n.use("zh_CN");
		ForgetPasswordVo forgetPasswordVo=c.getBean(ForgetPasswordVo.class,"p");
		String mobile=forgetPasswordVo.getMobile();
		if(StrKit.isBlank(mobile)||!ValidateUtil.validateMobilePhone(mobile)){
			addError("mobileMsg", res.get("forget.mobile.incorrect"));
		}
		String picCode=forgetPasswordVo.getPicCode();
		Integer codeResult=c.getSessionAttr(VertifyCodeService.VERTIFY_CODE);
		if(StrKit.isBlank(picCode)||codeResult==null||!picCode.equals(codeResult.toString())){
			addError("picCodeMsg",res.get("forget.piccode.incorrect"));
		}
		String msgCode=forgetPasswordVo.getMsgCode();
		String msgCodeResult=c.getSessionAttr(ForgetPasswordService.SMS_CODE);
		if(msgCodeResult==null){
			addError("msgCodeMsg", res.get("forget.msgcode.timeout"));
		}else if(StrKit.isBlank(msgCode)||msgCode.equals(msgCodeResult)){
			addError("msgCodeMsg", res.get("forget.msgcode.incorrect"));
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.keepBean(ForgetPasswordVo.class,"p");
		c.render("/pages/forget_password.html");
	}

}
