package com.dbkj.account.sys;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import com.google.gson.JsonObject;


public class PhoneUtil
{
	public static void sendSms(final String code,final String phone,final int type)
	{
		if(Config.sendSms.equals("true"))
		{
			Thread thread = new Thread()
			{
				public void run()
				{
					try
					{
						String reqid = getRanCodeId(32);
						JsonObject json = new JsonObject();
						json.addProperty("servId",Config.servId);
						json.addProperty("servAuth",Config.servAuth);
						json.addProperty("mobile",phone);
						json.addProperty("reqId",getRanCodeId(32));
						json.addProperty("action","10000");
						if(type==0)
						{
							json.addProperty("smsContent","你的短信验证码："+code);
						}
						else if(type==1)
						{
							json.addProperty("smsContent","你的密码："+code);
						}
						//json.put("longSms","2001");
						System.out.println(json.toString());
						URL url = new URL(Config.smsUrl);
						URLConnection connection = url.openConnection();
						connection.setConnectTimeout(5000);
						connection.setReadTimeout(5000);
						connection.setDoOutput(true);
						OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(),"utf-8");
						out.write(json.toString());
						out.flush();
						out.close();
						
						InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"utf-8");
						BufferedReader br = new BufferedReader(isr);
						StringBuffer sb = new StringBuffer();
						String str = null;
						while((str=br.readLine())!=null)
						{
							sb.append(str).append("\r\n");
						}
						br.close();
						String result = sb.toString();
						System.out.println(result);
						System.out.println("reqid："+reqid);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			};
			thread.start();
		}
	}
	
	public static String getRanCodeId(int len)
	{
		Random randGen = new Random();
		char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		char[] randBuffer = new char[len];
		for(int i=0;i<randBuffer.length;i++)
		{
			randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
		}
		return new String(randBuffer);
	}
}