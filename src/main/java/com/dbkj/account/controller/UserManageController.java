package com.dbkj.account.controller;

import com.jfinal.core.Controller;

public class UserManageController extends Controller{
	
	public void index(){
		render("/pages/manage/user/index.html");
	}

}
