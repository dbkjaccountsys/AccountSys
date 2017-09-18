package com.dbkj.account.validator;

import com.dbkj.account.util.ValidateUtil;
import com.dbkj.account.vo.RoleFormVo;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

public class AdminRoleValidator extends Validator{
	
	private final String ADD_ACTION="/manage/adminRole/save";
	private final String UPDATE_ACTION="/manage/adminRole/update";

	@Override
	protected void validate(Controller c) {
		String actionKey=getActionKey();
		RoleFormVo vo=c.getBean(RoleFormVo.class,"a");
		Res res=I18n.use("zh_CN");
		
		if(UPDATE_ACTION.equals(actionKey)&&vo.getId()==null){
			addError("idMsg", res.get("adminrole.id.empty"));
		}
		
		String roleName=vo.getRoleName();
		String roleNameMsg="roleNameMsg";
		if(StrKit.isBlank(roleName)){
			addError(roleNameMsg, res.get("adminrole.rolename.empty"));
		}else if(roleName.length()>15){
			addError(roleNameMsg, res.get("adminrole.rolename.length.more.than.15"));
		}else if(ValidateUtil.validateSpecialString(roleName)){
			addError(roleNameMsg, res.get("illegal.char.msg"));
		}
		
		String desc=vo.getDesc();
		String descMsg="descMsg";
		if(StrKit.isBlank(desc)){
			addError(descMsg, res.get("adminrole.desc.empty"));
		}else if(desc.length()>15){
			addError(roleNameMsg, res.get("adminrole.desc.length.more.than.15"));
		}else if(ValidateUtil.validateSpecialString(desc)){
			addError(roleNameMsg, res.get("illegal.char.msg"));
		}
		
		String operaAuth=vo.getOperaAuth();
		String operaAuthMsg="operaAuthMsg";
		if(StrKit.isBlank(operaAuth)){
			addError(operaAuthMsg, res.get("adminrole.opera.auth.empty"));
		}
		
		c.setAttr("a", vo);
	}

	@Override
	protected void handleError(Controller c) {
		c.keepBean(RoleFormVo.class,"a");
		if(getActionKey().equals(ADD_ACTION)){
			c.render("add.html");
		}else{
			c.render("edit.html");
		}
	}

}
