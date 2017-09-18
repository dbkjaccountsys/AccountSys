package com.dbkj.account.sys;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.plugin.activerecord.Db;

public class EmailResetCleanThread implements Runnable
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
				SimpleDateFormat sf = new SimpleDateFormat("HHmm");
				String time = sf.format(new Date());
				if(time.equals("0000"))
				{
					if(!isrun)
					{
						System.out.println("111111111111111111");
						String sql = "delete from user_info_reset where status=1";
						Db.update(sql);
						sql = "delete from user_info_reset where to_days(now())-to_days(time)>"+Config.modifyUserInfoDay;
						Db.update(sql);
						isrun = true;
						System.out.println("222222222222222222");
					}
				}
				else
				{
					isrun = false;
				}
				Thread.sleep(1000*30);
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
