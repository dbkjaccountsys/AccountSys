package com.dbkj.account.sys;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.dbkj.account.sys.form.UserAddTimeOutForm;
import com.dbkj.account.sys.form.UserForgetPasswordForm;
import com.dbkj.account.sys.form.UserLoginForm;
import com.dbkj.account.sys.form.UserPhoneForm;

public class MapValueThread implements Runnable
{
	public void run()
	{
		while(true)
		{
			try
			{
				Iterator<Map.Entry<String,UserPhoneForm>> es_phone = MapValue.map_phone.entrySet().iterator();
				while(es_phone.hasNext())
				{
					Map.Entry<String,UserPhoneForm> entry = es_phone.next();
					Date date = entry.getValue().getDate();
					if(new Date().getTime()-date.getTime()>1000*60*Integer.parseInt(Config.userAddCodeTime))
					{
						es_phone.remove();
					}
				}
				Iterator<Map.Entry<String,UserLoginForm>> es_login = MapValue.map_login.entrySet().iterator();
				while(es_login.hasNext())
				{
					Map.Entry<String,UserLoginForm> entry = es_login.next();
					Date date = entry.getValue().getDate();
					if(new Date().getTime()-date.getTime()>1000*60*Integer.parseInt(Config.loginErrorTime))
					{
						es_login.remove();
					}
				}
				Iterator<Map.Entry<String,UserAddTimeOutForm>> es_user_add = MapValue.map_user_add.entrySet().iterator();
				while(es_user_add.hasNext())
				{
					Map.Entry<String,UserAddTimeOutForm> entry = es_user_add.next();
					Date date = entry.getValue().getDate();
					if(new Date().getTime()-date.getTime()>1000*60*Integer.parseInt(Config.userAddSendPhoneTime))
					{
						es_user_add.remove();
					}
				}
				Iterator<Map.Entry<String,UserForgetPasswordForm>> es_user_forget = MapValue.map_user_forget.entrySet().iterator();
				while(es_user_forget.hasNext())
				{
					Map.Entry<String,UserForgetPasswordForm> entry = es_user_forget.next();
					Date date = entry.getValue().getDate();
					if(new Date().getTime()-date.getTime()>1000*60*Integer.parseInt(Config.forgetPasswordTime))
					{
						es_user_forget.remove();
					}
				}
				Thread.sleep(10000);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					Thread.sleep(5000);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

}
