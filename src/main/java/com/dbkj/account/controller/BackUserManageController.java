package com.dbkj.account.controller;

import java.util.List;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.AdminDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.Result;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.AdminRole;
import com.dbkj.account.service.AuthorityService;
import com.dbkj.account.service.BackUserManageService;
import com.dbkj.account.validator.BackUserValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;

public class BackUserManageController extends Controller{
	
	private AuthorityService authorityService;
	private BackUserManageService backUserManageService=new BackUserManageService();
	
	public void index(){
		authorityService=new AuthorityService();
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		boolean hasOpera=authorityService.isPermissionOfAdmin(admin.getRoleId(), "/manage/backUser/edit")||
				authorityService.isPermissionOfAdmin(admin.getRoleId(), "/manage/backUser/delete");
		setAttr("hasOpera", hasOpera);
		render("index.html");
	}

	public void list(){
		Page<AdminDto> page=new Page<AdminDto>();
		int pageNum=getParaToInt("page",1);
		page.setCurrentPage(pageNum);
		int rows=getParaToInt("rows",20);
		page.setPageSize(rows);
		
		String username=getPara("username");
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		backUserManageService.getAdminPage(page, username, admin);
		renderJson(page);
	}
	
	public void add(){
		List<AdminRole> rlist=backUserManageService.getAdminRoleList();
		setAttr("rlist", rlist);
		render("add.html");
	}
	
	public void existUsername(){
		String username=getPara("b.username");
		boolean result=backUserManageService.existsUsername(username);
		renderJson(!result);
	}
	
	@Before({POST.class,BackUserValidator.class})
	public void save(){
		AdminDto dto=getAttr("b");
		boolean result=backUserManageService.addUser(dto, getRequest());
		if(result){
			redirect("/manage/backUser");
		}else{
			setAttr("errorMsg", "操作失败！");
			setAttr("b", dto);
			List<AdminRole> rlist=backUserManageService.getAdminRoleList();
			setAttr("rlist", rlist);
			render("add.html");
		}
	}
	
	public void edit(){
		Long id=getParaToLong("id");
		AdminDto dto=backUserManageService.getUser(id);
		setAttr("b", dto);
		List<AdminRole> rlist=backUserManageService.getAdminRoleList();
		setAttr("rlist", rlist);
		render("edit.html");
	}
	
	@Before({POST.class,BackUserValidator.class})
	public void update(){
		AdminDto dto=getAttr("b");
		boolean result=backUserManageService.updateUser(dto, getRequest());
		if(result){
			redirect("/manage/backUser");
		}else{
			setAttr("errorMsg", "操作失败！");
			setAttr("b", dto);
			List<AdminRole> rlist=backUserManageService.getAdminRoleList();
			setAttr("rlist", rlist);
			render("add.html");
		}
	}
	
	public void delete(){
		Long id=getParaToLong("id");
		Result result = backUserManageService.deleteUser(id,getRequest());
		renderJson(result);
	}
}
