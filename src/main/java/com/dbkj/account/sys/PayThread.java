package com.dbkj.account.sys;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.plugin.activerecord.Db;

public class PayThread implements Runnable
{
	public boolean isrun = false;
	
	public void run()
	{
		try
		{
			Thread.sleep(1000*30);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		while(true)
		{
			try
			{
				String sql = "update user_recharge set status=1 where status=0 and timestampdiff(minute,time,now())>="+Config.timeoutExpress;
				Db.update(sql);
				Thread.sleep(1000*60*10);
				System.out.println("=============PayThread=============");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					Thread.sleep(10000);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

}
