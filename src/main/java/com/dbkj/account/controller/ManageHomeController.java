package com.dbkj.account.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.MenuNode;
import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.model.Admin;
import com.dbkj.account.service.ManageHomeService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;

@Clear({AuthInterceptor.class})
public class ManageHomeController extends Controller{
	
	private ManageHomeService homeService=new ManageHomeService();
	
	public void index(){
		render("/pages/manage/home.html");
		
	}

	@Before({CacheInterceptor.class})
	@CacheName(ManageHomeService.ADMIN_MENU_CACHE)
	public void adminMenu(){
		Object obj=getSessionAttr(Constant.CURRENT_USER);
		Long roleId=0L;
		if(obj!=null&&obj instanceof Admin){
			Admin admin = (Admin)obj;
			roleId=admin.getRoleId();
		}
		List<MenuNode> mlist=homeService.getAdminMenuList(roleId);
		String jsonStr=JSON.toJSONString(mlist);
//		System.out.println(jsonStr);
		renderJson(jsonStr);
	}
}
