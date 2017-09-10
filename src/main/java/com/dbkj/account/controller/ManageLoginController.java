package com.dbkj.account.controller;

import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.interceptor.LoginInterceptor;
import com.dbkj.account.service.ManageHomeService;
import com.dbkj.account.service.ManageLoginService;
import com.dbkj.account.validator.LoginValidator;
import com.dbkj.account.vo.LoginVo;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;

@Clear({LoginInterceptor.class,AuthInterceptor.class})
public class ManageLoginController extends Controller{
	
	private ManageLoginService manageLoginService=new ManageLoginService();
	
	public void index(){
		render("/pages/manage/login.html");
	}
	
	@ActionKey("/manage/dologin")
	@Before({POST.class,LoginValidator.class})
	public void login(){
		LoginVo loginVo=getBean(LoginVo.class,"user");
//		//判断是否登陆错误次数过多
//		boolean result=manageLoginService.isLoginTooManyTimes(loginVo.getPassword(), getRequest());
//		if(result){
//			redirect("/forget");
//		}else{
//			result=manageLoginService.login(loginVo, getRequest());
//			if(result){//登陆成功
//				redirect("/");
//			}else{
//				render("/pages/manage/login.html");
//			}
//		}
		boolean result=manageLoginService.login(loginVo, getRequest());
		if(result){//登陆成功
			redirect("/manage");
		}else{
			render("/pages/manage/login.html");
		}
	}
	
	@CacheName(ManageHomeService.ADMIN_MENU_CACHE)
	@Before({EvictInterceptor.class})
	@ActionKey("/manage/logout")
	public void logout(){
		manageLoginService.logout(getSession());
		redirect("/manage/login");
	}
}
