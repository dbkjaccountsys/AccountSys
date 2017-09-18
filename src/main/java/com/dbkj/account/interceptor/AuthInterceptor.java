package com.dbkj.account.interceptor;

import javax.servlet.http.HttpServletResponse;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.User;
import com.dbkj.account.service.AuthorityService;
import com.dbkj.account.util.WebUtil;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class AuthInterceptor implements Interceptor{
	
	private AuthorityService authorityService=new AuthorityService();

	public void intercept(Invocation inv) {
		Controller controller=inv.getController();
		String action=inv.getActionKey();
		Object obj=controller.getSessionAttr(Constant.CURRENT_USER);
		//判断是否有当前操作权�?
		boolean result=false;
		if(obj instanceof Admin){
			Admin admin=(Admin)obj;
			result=authorityService.isPermissionOfAdmin(admin.getRoleId(), action);
		}else{
			User user=(User)obj;
			result=authorityService.isPermissionOfUser(user.getRoleId(), action);
		}
		if(result){
			inv.invoke();
		}else{
			//判断当前请求为是否为ajax请求
			if(WebUtil.isAjaxRequest(controller.getRequest())){
				HttpServletResponse response=controller.getResponse();
				response.setStatus(403);
				controller.renderNull();
			}else{
				controller.renderError(401, "/pages/other/401.html");
			}
		}
		
	}

}
