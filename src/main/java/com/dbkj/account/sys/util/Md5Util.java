package com.dbkj.account.sys.util;

import java.security.MessageDigest;

public class Md5Util
{
	public static String getMd5Encode(String str)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer();
			for(int offset=0;offset<b.length;offset++)
			{
				i = b[offset];
				if(i<0)
				{
					i += 256;
				}
				if(i<16)
				{
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}   
			return buf.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	public static void main(String args[])
	{
		System.out.println(getMd5Encode("admin"));
	}
}