package com.dbkj.account.controller;

import com.jfinal.core.Controller;

public class FrontUserManageController extends Controller{
	
	public void index(){
		render("/pages/manage/user/index.html");
	}

}
