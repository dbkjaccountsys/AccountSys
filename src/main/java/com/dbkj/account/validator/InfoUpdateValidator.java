package com.dbkj.account.validator;

import com.dbkj.account.util.ValidateUtil;
import com.dbkj.account.vo.InfoFormVo;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

public class InfoUpdateValidator extends Validator{

	@Override
	protected void validate(Controller c) {
		InfoFormVo vo=c.getBean(InfoFormVo.class,"user");
		Res res=I18n.use("zh_CN");
		
		String name=vo.getName();
		String nameMsg="nameMsg";
		if(!StrKit.isBlank(name)){
			if(name.length()>15){
				addError(nameMsg, res.get("info.name.length.more.than.15"));
			}else if(ValidateUtil.validateSpecialString(name)){
				addError(nameMsg, res.get("illegal.char.msg"));
			}
		}
		
		String phone=vo.getPhone();
		String phoneMsg="phoneMsg";
		if(!StrKit.isBlank(phone)&&!ValidateUtil.validateMobilePhone(phone)){
			addError(phoneMsg, res.get("mobile.format.incorrect"));
		}
		
		String email=vo.getEmail();
		String emailMsg="emailMsg";
		if(!StrKit.isBlank(email)&&!ValidateUtil.validateEmail(email)){
			addError(emailMsg, res.get("email.format.incorrect"));
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.keepBean(InfoFormVo.class,"user");
		c.render("edit_info.html");
	}

}
