package com.dbkj.account.validator;

import java.util.List;

import com.dbkj.account.dto.AdminDto;
import com.dbkj.account.model.AdminRole;
import com.dbkj.account.service.BackUserManageService;
import com.dbkj.account.util.ValidateUtil;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

public class BackUserValidator extends Validator{
	
	private final String updateAction="/manage/backUser/update";
	private final String addAction="/manage/backUser/save";
	
	private BackUserManageService userService=new BackUserManageService();

	@Override
	protected void validate(Controller c) {
		String actionKey=getActionKey();
		AdminDto dto=c.getBean(AdminDto.class,"b");
		Res res=I18n.use("zh_CN");
		if(updateAction.equals(actionKey)&&dto.getId()==null){
			addError("idMsg", res.get("backuser.id.empty"));
		}
		
		String username=dto.getUsername();
		String usernameMsg="usernameMsg";
		if(addAction.equals(actionKey)){
			if(StrKit.isBlank(username)){
				addError(usernameMsg, res.get("backuser.username.empty"));
			}else if(ValidateUtil.validateSpecialString(username)){
				addError(usernameMsg, res.get("illegal.char.msg"));
			}else if(userService.existsUsername(username)){
				addError(usernameMsg, res.get("backuser.username.repeat"));
			}
		}
		
		String password=dto.getPassword();
		String passwordMsg="passwordMsg";
		if(StrKit.isBlank(password)){
			addError(passwordMsg, res.get("backuser.password.empty"));
		}else if(!ValidateUtil.validatePassword(password)){
			addError(passwordMsg, res.get("password.format.incorrect"));
		}
		
		String confirmPassword=dto.getConfirmPassword();
		String confirmPasswordMsg="confirmPasswordMsg";
		if(StrKit.isBlank(confirmPassword)){
			addError(confirmPasswordMsg, res.get("backuser.confirm.password.empty"));
		}else if(!password.equals(confirmPassword)){
			addError(confirmPasswordMsg, res.get("backuser.confirm.not.equal.password"));
		}
		
		String name=dto.getName();
		String nameMsg="nameMsg";
		if(!StrKit.isBlank(name)&&ValidateUtil.validateSpecialString(name)){
			addError(nameMsg, res.get("illegal.char.msg"));
		}
		
		String phone=dto.getPhone();
		String phoneMsg="phoneMsg";
		if(StrKit.isBlank(phone)){
			addError(phoneMsg, res.get("backuser.phone.empty"));
		}else if(!ValidateUtil.validateMobilePhone(phone)){
			addError(phoneMsg, res.get("mobile.format.incorrect"));
		}
		
		String email=dto.getEmail();
		String emailMsg="emailMsg";
		if(StrKit.isBlank(email)){
			addError(emailMsg, res.get("backuser.email.empty"));
		}else if(!ValidateUtil.validateEmail(email)){
			addError(emailMsg, res.get("email.format.incorrect"));
		}
		
		Long roleId=dto.getRoleId();
		String roleIdMsg="roleIdMsg";
		if(roleId==null){
			addError(roleIdMsg, res.get("backuser.roleid.empty"));
		}
		c.setAttr("b", dto);
	}

	@Override
	protected void handleError(Controller c) {
		c.keepBean(AdminDto.class,"b");
		List<AdminRole> rlist=userService.getAdminRoleList();
		c.setAttr("rlist", rlist);
		if(updateAction.equals(getActionKey())){
			c.render("edit.html");
		}else{
			c.render("add.html");
		}
	}

}
