package com.dbkj.account.template;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.dic.UserType;
import com.dbkj.account.service.AuthorityService;
import com.dbkj.account.util.ValidateUtil;
import com.jfinal.kit.StrKit;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class AuthorityDirective implements TemplateDirectiveModel{
	
	private UserType userType;
	
	public AuthorityDirective(){
		userType=UserType.USER;
	}
	
	public AuthorityDirective(UserType userType){
		this.userType=userType;
	}
	
	private static final Logger logger=LoggerFactory.getLogger(AuthorityDirective.class);
	
	private AuthorityService authorityService=new AuthorityService();
	
	private static final String PARA_ROLE_ID="role_id";
	
	private static final String PARA_ACTION="action";

	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		if(params==null||params.size()==0||!params.containsKey(AuthorityDirective.PARA_ROLE_ID)||!params.containsKey(AuthorityDirective.PARA_ACTION)){
			if(logger.isErrorEnabled()){
				logger.error("缺少必要的参数");
			}
			throw new TemplateModelException("缺少必要的参数");
		}
		String roleId=params.get(AuthorityDirective.PARA_ROLE_ID)!=null?params.get(AuthorityDirective.PARA_ROLE_ID).toString():"";
		String url=params.get(AuthorityDirective.PARA_ACTION)!=null?params.get(AuthorityDirective.PARA_ACTION).toString():"";
		
		if(!StrKit.isBlank(roleId)&&ValidateUtil.isNumeric(roleId)){
			try{
				Long rid=Long.parseLong(roleId);
				boolean result=false;
				if(userType.equals(UserType.ADMIN)){
					result=authorityService.isPermissionOfAdmin(rid, url);
				}else{
					result=authorityService.isPermissionOfUser(rid, url);
				}
				if(result){
					body.render(env.getOut());
				}
			}catch(Exception e){
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
				throw new RuntimeException(e);
			}
		}else{
			throw new TemplateModelException("参数有误");
		}
	}

}
