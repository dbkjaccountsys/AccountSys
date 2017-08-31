package com.dbkj.account.interceptor;

import com.dbkj.account.dic.UserType;
import com.dbkj.account.template.AuthorityDirective;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class AdminAuthorityTemplateDirectiveInterceptor implements Interceptor{

	private static final String LABEL_HAS_AUTHORITY="_has_admin_authority";
	
	public void intercept(Invocation inv) {
		Controller controller=inv.getController();
		controller.setAttr(LABEL_HAS_AUTHORITY, new AuthorityDirective(UserType.ADMIN));
		inv.invoke();
	}

}
