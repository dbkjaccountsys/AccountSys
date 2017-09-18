package com.dbkj.account.sys;

import com.dbkj.account.interceptor.AdminAuthorityTemplateDirectiveInterceptor;
import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.interceptor.LoginInterceptor;
import com.dbkj.account.interceptor.ResetPasswordInterceptor;
import com.dbkj.account.interceptor.UserAuthorityTemplateDirectiveInterceptor;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

@Clear({LoginInterceptor.class,AuthInterceptor.class,AdminAuthorityTemplateDirectiveInterceptor.class,UserAuthorityTemplateDirectiveInterceptor.class,ResetPasswordInterceptor.class})
public class NewController extends Controller
{
	public void index()
	{
		setAttr("count",Config.modifyEmailPhoneCount);
		render("new/index.html");
	}
}
