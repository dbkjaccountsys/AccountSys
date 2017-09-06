package com.dbkj.account.controller;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.FavorableDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.Result;
import com.dbkj.account.model.Admin;
import com.dbkj.account.service.FavorableService;
import com.dbkj.account.validator.FavorableValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;

/**
 * 优惠相关操作
 * @author DELL
 *
 */
public class FavorableController extends Controller{
	
	private FavorableService favorableService=new FavorableService();
	
	public void index(){
		render("index.html");
	}

	public void list(){
		Page<FavorableDto> page=new Page<FavorableDto>();
		int pageNum=getParaToInt("page",1);
		page.setCurrentPage(pageNum);
		int rows=getParaToInt("rows",20);
		page.setPageSize(rows);
		
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		long roleId=admin.getRoleId();
		favorableService.getPage(page,roleId);
		renderJson(page);
	}
	
	public void add(){
		render("add.html");
	}
	
	@Before({POST.class,FavorableValidator.class})
	public void save(){
		FavorableDto dto=getBean(FavorableDto.class,"f");
		favorableService.addFavorable(dto, getRequest());
		redirect("/manage/favorable");
	}
	
	public void edit(){
		long id=getParaToLong();
		FavorableDto f=favorableService.getFavorable(id);
		setAttr("f", f);
		render("edit.html");
	}
	
	@Before({POST.class,FavorableValidator.class})
	public void update(){
		FavorableDto dto=getBean(FavorableDto.class,"f");
		favorableService.updateFavorable(dto, getRequest());
		redirect("/manage/favorable");
	}
	
	public void cancel(){
		long id=getParaToLong("id");
		Result result=favorableService.cancelFavorable(id,getRequest());
		renderJson(result);
	}
}
