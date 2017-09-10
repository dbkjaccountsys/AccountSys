package com.dbkj.account.controller;

import java.util.List;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.AdminRoleDto;
import com.dbkj.account.dto.AuthNode;
import com.dbkj.account.dto.Page;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.AdminRole;
import com.dbkj.account.service.AdminRoleManageService;
import com.dbkj.account.service.AuthorityService;
import com.dbkj.account.validator.AdminRoleValidator;
import com.dbkj.account.vo.RoleFormVo;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;

public class AdminRoleManageController extends Controller{

	private AuthorityService authorityService;
	private AdminRoleManageService service=new AdminRoleManageService();
	
	public void index(){
		authorityService=new AuthorityService();
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		boolean hasOpera=authorityService.isPermissionOfAdmin(admin.getRoleId(), "/manage/adminRole/edit");
		setAttr("hasOpera", hasOpera);
		render("index.html");
	}
	
	public void list(){
		Page<AdminRoleDto> page=new Page<AdminRoleDto>();
		int pageNum=getParaToInt("page",1);
		page.setCurrentPage(pageNum);
		int rows=getParaToInt("rows",20);
		page.setPageSize(rows);
		
		String desc=getPara("desc");
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		service.getRolePage(page, desc,admin.getRoleId());
		renderJson(page);
	}
	
	public void auths(){
		Long roleId=getParaToLong("id");
		List<AuthNode> list=service.getAdminAuths(roleId);
		renderJson(list);
	}
	
	public void add(){
		render("add.html");
	}
	
	public void existRoleName(){
		Long id=getParaToLong("id");
		String roleName=getPara("roleName");
		boolean result=service.isExistRoleName(roleName, id);
		renderJson(result);
	}
	
	@Before({POST.class,AdminRoleValidator.class})
	public void save(){
		RoleFormVo vo=getAttr("a");
		boolean result=service.addRole(vo, getRequest());
		if(result){
			redirect("/manage/adminRole");
		}else{
			setAttr("errorMsg", "操作失败！");
			setAttr("a", vo);
			render("add.html");
		}
	}
	
	public void edit(){
		Long roleId=getParaToLong("id");
		RoleFormVo vo=service.getAdminRole(roleId);
		setAttr("a", vo);
		render("edit.html");
	}
	
	@Before({POST.class,AdminRoleValidator.class})
	public void update(){
		RoleFormVo vo=getAttr("a");
		boolean result=service.updateAdminRole(vo, getRequest());
		if(result){
			redirect("/manage/adminRole");
		}else{
			setAttr("errorMsg", "操作失败！");
			setAttr("a", vo);
			render("add.html");
		}
	}
}
