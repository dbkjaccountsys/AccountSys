package com.dbkj.account.validator;

import com.dbkj.account.service.VertifyCodeService;
import com.dbkj.account.vo.LoginVo;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

public class LoginValidator extends Validator{
	
	private VertifyCodeService vertifyCodeService=new VertifyCodeService();

	@Override
	protected void validate(Controller c) {
		LoginVo loginVo=c.getBean(LoginVo.class,"user");
		Res res=I18n.use("zh_CN");
		if(StrKit.isBlank(loginVo.getUsername())){
			addError("usernameMsg", res.get("login.username.empty"));
		}
		
		if(StrKit.isBlank(loginVo.getPassword())){
			addError("passwordMsg", res.get("login.password.empty"));
		}
		
		if(StrKit.isBlank(loginVo.getVertifyCode())){
			addError("vertifyCodeMsg", res.get("login.vertifycode.empty"));
		}else if(!vertifyCodeService.validate(loginVo.getVertifyCode(), c)){
			addError("vertifyCodeMsg", res.get("login.vertifycode.incorrect"));
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.keepBean(LoginVo.class, "user");
		c.render("/pages/manage/login.html");
	}	

}
