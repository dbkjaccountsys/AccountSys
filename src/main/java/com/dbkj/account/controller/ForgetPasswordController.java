package com.dbkj.account.controller;

import com.dbkj.account.dto.Result;
import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.interceptor.LoginInterceptor;
import com.dbkj.account.service.ForgetPasswordService;
import com.dbkj.account.validator.ForgetPasswordValidator;
import com.dbkj.account.vo.ForgetPasswordVo;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;

@Clear({LoginInterceptor.class,AuthInterceptor.class})
public class ForgetPasswordController extends Controller{
	
	private ForgetPasswordService getBackPasswordService=new ForgetPasswordService();

	public void index(){
		render("/pages/forget_password.html");
	}
	
	@Before({POST.class,ForgetPasswordValidator.class})
	public void validate(){
		setSessionAttr(ForgetPasswordService.IS_VALIATE_MOBILE, true);
		redirect("");
	}
	
	@Before({POST.class})
	public void getCode(){
		String mobile=getPara("mobile");
		String code=getPara("code");
		Result<?> result=getBackPasswordService.getCode(mobile, code, this);
		renderJson(result);
	}
}
