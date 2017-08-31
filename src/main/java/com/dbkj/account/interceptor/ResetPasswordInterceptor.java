package com.dbkj.account.interceptor;

import com.dbkj.account.service.ForgetPasswordService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class ResetPasswordInterceptor implements Interceptor{

	public void intercept(Invocation inv) {
		//判断是否验证手机号码
		Controller controller = inv.getController();
		Object obj=controller.getSessionAttr(ForgetPasswordService.IS_VALIATE_MOBILE);
		if(obj==null){
			controller.render("/pages/other/404.html", 404);
		}else{
			inv.invoke();
		}
	}

}
