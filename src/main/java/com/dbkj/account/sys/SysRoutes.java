package com.dbkj.account.sys;

import com.jfinal.config.Routes;

public class SysRoutes extends Routes
{
	@Override
	public void config()
	{
		add("/user",UserController.class,"/pages/sys");
		add("/new",NewController.class,"/pages/sys");
		add("/money",MoneyController.class,"/pages/sys");
	}
}
