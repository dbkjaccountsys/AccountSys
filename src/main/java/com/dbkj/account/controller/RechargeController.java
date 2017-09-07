package com.dbkj.account.controller;

import java.util.List;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.CompanyDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.RechargeDto;
import com.dbkj.account.model.Admin;
import com.dbkj.account.service.AuthorityService;
import com.dbkj.account.service.RechargeService;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;

/**
 * 充值相关操作
 * @author DELL
 *
 */
public class RechargeController extends Controller{
	
	private AuthorityService authorityService;
	private RechargeService rechargeService=new RechargeService();

	@CacheName(Constant.COMMON_CACHE_KEY)
	@Before({CacheInterceptor.class})
	public void index(){
		List<CompanyDto> list = rechargeService.getCompanyList();
		setAttr("clist", list);
		//判断是否有操作权限
		authorityService=new AuthorityService();
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		boolean hasOpera=authorityService.isPermissionOfAdmin(admin.getRoleId(), "/manage/recharge/charging");
		setAttr("hasOpera", hasOpera);
		render("index.html");
	}
	
	public void list(){
		Long uid=getParaToLong("uid");
		Page<RechargeDto> page=new Page<RechargeDto>();
		int pageNum=getParaToInt("page",1);
		page.setCurrentPage(pageNum);
		int rows=getParaToInt("rows",20);
		page.setPageSize(rows);
		
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		rechargeService.getList(page, uid,admin.getRoleId());
		renderJson(page);
	}
	
	
	public void charging(){
		Long uid=getParaToLong("uid");
		RechargeDto dto=rechargeService.getRechargeInfo(uid);
		setAttr("r", dto);
		render("charge.html");
	}
	
	public void charge(){
		RechargeDto dto=getBean(RechargeDto.class,"r");
		String str=rechargeService.validateCharge(dto);
		if(null==str){//验证失败
			setAttr("errorMsg", str);
			setAttr("r", dto);
			render("charge.html");
			return;
		}else{
			rechargeService.charge(dto, getRequest());
			redirect("/manage/recharge");
		}
	}
}
