package com.dbkj.account.sys;

import com.jfinal.config.Routes;

public class SysRoutes extends Routes
{
	@Override
	public void config()
	{
		//me.add("/address",AddressController.class,"/pages/address");
		add("/user",UserController.class,"/pages/sys");
	}
}
