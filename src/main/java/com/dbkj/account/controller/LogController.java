package com.dbkj.account.controller;

import java.util.List;

import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dic.UserType;
import com.dbkj.account.dto.LogDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.model.OperaType;
import com.dbkj.account.service.LogService;
import com.jfinal.core.Controller;

public class LogController extends Controller{
	
	private LogService logService=new LogService();
	
	public void index(){
		List<Integer> months=logService.getLogMonth();
		setAttr("months", months);
		List<UserType> userTypes=logService.getUserTypes();
		setAttr("userTypes", userTypes);
		List<OperaResult> operaResults=logService.getOperaResults();
		setAttr("operaResults", operaResults);
		List<OperaType> operaTypes=logService.getOperaTypes();
		setAttr("operaTypes", operaTypes);
		render("index.html");
	}
	
	public void list(){
		Page<LogDto> page=new Page<LogDto>();
		int pageNum=getParaToInt("page",1);
		page.setCurrentPage(pageNum);
		int rows=getParaToInt("rows",20);
		page.setPageSize(rows);
		
		String month=getPara("month");
		int userType=getParaToInt("userType",UserType.ADMIN.getValue());
		int operaResult=getParaToInt("operaResult",OperaResult.SUCCESS.getValue());
		String username=getPara("username");
		String startTime=getPara("startTime");
		String endTime=getPara("endTime");
		int operaType=getParaToInt("operaType", 0);
		logService.getPage(page, month, userType, operaResult, username, startTime, endTime,operaType);
		renderJson(page);
	}

}
