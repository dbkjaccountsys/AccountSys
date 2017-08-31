package com.dbkj.account.sys;

import java.util.HashMap;
import java.util.Map;

import com.dbkj.account.sys.form.UserEmailForm;
import com.dbkj.account.sys.form.UserLoginForm;
import com.dbkj.account.sys.form.UserPhoneForm;

public class MapValue
{
	public static Map<String,UserPhoneForm> map_phone = new HashMap<String,UserPhoneForm>();
	
	public static Map<String,UserEmailForm> map_email = new HashMap<String,UserEmailForm>();
	
	public static Map<String,UserLoginForm> map_login = new HashMap<String,UserLoginForm>();
}