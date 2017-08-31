package com.dbkj.account.controller;

import java.io.File;

import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.interceptor.LoginInterceptor;
import com.dbkj.account.service.VertifyCodeService;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

@Clear({LoginInterceptor.class,AuthInterceptor.class})
public class VertifyCodeController extends Controller{
	
	private VertifyCodeService vertifyCodeService=new VertifyCodeService();
	
	public void index(){
		File file=vertifyCodeService.getVertifyCode(this);
		renderFile(file);
	}
	
	public void validate(){
		String code=getPara("user.vertifyCode");
		boolean result=vertifyCodeService.validate(code, this);
		renderJson(result);
	}

}
