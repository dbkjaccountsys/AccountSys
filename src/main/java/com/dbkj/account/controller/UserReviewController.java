package com.dbkj.account.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.UserReviewDto;
import com.dbkj.account.model.Admin;
import com.dbkj.account.service.UserReviewService;
import com.jfinal.core.Controller;

public class UserReviewController extends Controller{
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	private UserReviewService userReviewService=new UserReviewService();

	
	public void index(){
		render("/pages/manage/user_review/index.html");
	}
	
	public void list(){
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		Page<UserReviewDto> page=new Page<>();
		int pageNum=getParaToInt("page",1);
		page.setCurrentPage(pageNum);
		int rows=getParaToInt("rows",20);
		page.setPageSize(rows);
		
		String fromDate=getPara("fromDate");
		String toDate=getPara("toDate");
		String username=getPara("username");
		userReviewService.getPage(page,fromDate,toDate,username,admin.getRoleId());
		renderJson(page);
	}
	
	public void userinfo(){
		render("userinfo.html");
	}
}
