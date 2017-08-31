package com.dbkj.account.controller;

import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.interceptor.LoginInterceptor;
import com.dbkj.account.interceptor.ResetPasswordInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

@Before({ResetPasswordInterceptor.class})
@Clear({LoginInterceptor.class,AuthInterceptor.class})
public class ResetPasswordController extends Controller{
	
	public void index(){
		render("/pages/reset_password.html");
	}

}
