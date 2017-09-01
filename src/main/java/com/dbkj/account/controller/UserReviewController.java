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
	
	private Admin getAdmin(){
		return getSessionAttr(Constant.CURRENT_USER);
	}

	
	public void index(){
		render("/pages/manage/user_review/index.html");
	}
	
	public void list(){
		Admin admin=getAdmin();
		Page<UserReviewDto> page=new Page<UserReviewDto>();
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
		long id=getParaToLong();
		UserReviewDto userReviewDto=userReviewService.getUserInfo(id);
		setAttr("u", userReviewDto);
		render("userinfo.html");
	}
	
	public void review(){
		long uid=getAdmin().getId();
		UserReviewDto userReviewDto=getBean(UserReviewDto.class,"u");
		boolean result=userReviewService.validateReview(userReviewDto);
		if(result){
			result = userReviewService.review(userReviewDto,getRequest(), uid);
			if(result){
				redirect("/manage/userReview");
				return;
			}else{
				setAttr("errorMsg", "操作失败！");
			}
		}else{//验证不通过
			setAttr("errorMsg", "缺少必要的参数");
		}
		UserReviewDto u = userReviewService.getUserInfo(userReviewDto.getId());
		u.setIspass(userReviewDto.getIspass());
		u.setRemark(userReviewDto.getRemark());
		setAttr("u", u);
		render("userinfo.html");
	}
	
}
