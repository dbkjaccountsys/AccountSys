package com.dbkj.account.sys;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.dbkj.account.sys.form.UserEmailForm;
import com.dbkj.account.sys.form.UserLoginForm;
import com.dbkj.account.sys.form.UserPhoneForm;

public class UserPhoneThread implements Runnable
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
					if(new Date().getTime()-date.getTime()>1000*60*3)
					{
						es_phone.remove();
					}
				}
				Iterator<Map.Entry<String,UserEmailForm>> es_email = MapValue.map_email.entrySet().iterator();
				while(es_email.hasNext())
				{
					Map.Entry<String,UserEmailForm> entry = es_email.next();
					Date date = entry.getValue().getDate();
					if(new Date().getTime()-date.getTime()>1000*60*3)
					{
						es_email.remove();
					}
				}
				Iterator<Map.Entry<String,UserLoginForm>> es_login = MapValue.map_login.entrySet().iterator();
				while(es_login.hasNext())
				{
					Map.Entry<String,UserLoginForm> entry = es_login.next();
					Date date = entry.getValue().getDate();
					if(new Date().getTime()-date.getTime()>1000*60*3)
					{
						es_login.remove();
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
