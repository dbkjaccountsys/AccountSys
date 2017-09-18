package com.dbkj.account.controller;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.FrontUserDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.Result;
import com.dbkj.account.model.Admin;
import com.dbkj.account.service.AuthorityService;
import com.dbkj.account.service.FrontUserManageService;
import com.dbkj.account.validator.FrontUserValidator;
import com.dbkj.account.vo.FrontUserFormVo;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;

public class FrontUserManageController extends Controller{
	
	private FrontUserManageService frontUserManageService=new FrontUserManageService();
	private AuthorityService authorityService;
	
	public void index(){
		Admin admin =getSessionAttr(Constant.CURRENT_USER);
		authorityService=new AuthorityService();
		long roleId=admin.getRoleId();
		boolean hasOpera=authorityService.isPermissionOfAdmin(roleId, "/manage/frontUser/delete")||
					authorityService.isPermissionOfAdmin(roleId, "/manage/frontUser/edit");
		setAttr("hasOpera", hasOpera);
		render("index.html");
	}
	
	public void list(){
		Page<FrontUserDto> page=new Page<FrontUserDto>();
		int pageNum=getParaToInt("page",1);
		page.setCurrentPage(pageNum);
		int rows=getParaToInt("rows",20);
		page.setPageSize(rows);
		
		String username=getPara("username");
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		frontUserManageService.getUserPage(page, username, admin.getRoleId());
		renderJson(page);
	}
	
	public void add(){
		render("add.html");
	}
	
	@Before({POST.class,FrontUserValidator.class})
	public void save(){
		FrontUserFormVo vo=getAttr("f");
		boolean result=frontUserManageService.addUser(vo, getRequest());
		if(result){
			redirect("/manage/frontUser");
		}else{
			setAttr("errorMsg", "操作失败！");
			setAttr("f", vo);
			render("add.html");
		}
	}
	
	public void existsPhone(){
		String phone=getPara("f.phone");
		boolean result=frontUserManageService.existsPhone(phone);
		renderJson(!result);
	}
	
	public void edit(){
		Long id=getParaToLong("id");
		FrontUserFormVo vo=frontUserManageService.getUserInfo(id);
		setAttr("f", vo);
		render("edit.html");
	}
	
	@Before({POST.class,FrontUserValidator.class})
	public void update(){
		FrontUserFormVo vo=getAttr("f");
		boolean result=frontUserManageService.updateUser(vo, getRequest());
		if(result){
			redirect("/manage/frontUser");
		}else{
			setAttr("errorMsg", "操作失败！");
			setAttr("f", vo);
			render("edit.html");
		}
	}
	
	public void delete(){
		Long id=getParaToLong("id");
		Result result=frontUserManageService.deleteUser(id, getRequest());
		renderJson(result);
	}
}
