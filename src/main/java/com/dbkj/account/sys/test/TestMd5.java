package com.dbkj.account.sys.test;

import com.dbkj.account.sys.util.Md5Util;

public class TestMd5
{
	public static void main(String[] args)
	{
		try
		{
			String str = "123456";
			System.out.println(str);
			System.out.println(Md5Util.getMd5Encode(str));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}