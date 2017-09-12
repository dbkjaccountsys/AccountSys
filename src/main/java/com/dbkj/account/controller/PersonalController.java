package com.dbkj.account.controller;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.Result;
import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.model.Admin;
import com.dbkj.account.service.PersonalService;
import com.dbkj.account.validator.InfoUpdateValidator;
import com.dbkj.account.vo.InfoFormVo;
import com.dbkj.account.vo.UpdatePwdFormVo;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;

@Clear({AuthInterceptor.class})
public class PersonalController extends Controller{
	
	private PersonalService personalService=new PersonalService();
	
	public void editPassword(){
		render("edit_pwd.html");
	}
	
	public void updatePassword(){
		UpdatePwdFormVo formVo=getBean(UpdatePwdFormVo.class,"p");
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		Result result=personalService.updatePassword(formVo,admin,getRequest());
		renderJson(result);
	}
	
	public void info(){
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		setAttr("user", personalService.getAdmin(admin.getId()));
		render("info.html");
	}
	
	public void editInfo(){
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		setAttr("user", personalService.getAdmin(admin.getId()));
		render("edit_info.html");
	}
	
	@Before({POST.class,InfoUpdateValidator.class})
	public void updateInfo(){
		InfoFormVo info=getBean(InfoFormVo.class,"user");
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		boolean result=personalService.updateInfo(info, admin.getId(), getRequest());
		if(result){
			redirect("/manage/personal/info");
		}else{
			setAttr("errorMsg", "操作失败");
			render("edit_info.html");
		}
	}
}
