package com.dbkj.account.interceptor;

import com.dbkj.account.dic.Constant;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class LoginInterceptor implements Interceptor{

	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		Object obj=controller.getSessionAttr(Constant.CURRENT_USER);
		
		if(obj==null){//未登录
			String actionKey = inv.getActionKey();
			//管理员
			if(actionKey.startsWith("/manage")){
				controller.redirect("/manage/login");
			}else{
				controller.redirect("/login");
			}
			
		}else{
			controller.setAttr("c_user", obj);
			inv.invoke();
		}
	}

}
