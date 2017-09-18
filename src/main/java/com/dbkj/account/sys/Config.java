package com.dbkj.account.sys;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.jfinal.kit.PropKit;

public class Config
{
	public static String jdbcUrl = "";
	
	public static String username = "";
	
	public static String password = "";
		
	public static String servId = "";
	
	public static String servAuth = "";
	
	public static String smsUrl = "";
	
	public static String mailHost = "";
	
	public static String mailAuth = "";
	
	public static String mailForm = "";
	
	public static String mailUsername = "";
	
	public static String mailPassword = "";
	
	public static String mailTransport = "";
	
	public static String sendEmail = "";
	
	public static String sendSms = "";
	
	public static String emailChangeUrl = "";
	
	public static String forgetPasswordCount = "";
	
	public static String forgetPasswordTime = "";
	
	public static String modifyEmailPhoneCount = "";
	
	public static String modifyPhoneTime = "";
	
	public static String loginErrorCount = "";
	
	public static String userAddSendPhoneCount = "";
	
	public static String loginErrorTime = "";
	
	public static String userAddSendPhoneTime = "";
	
	public static String userAddCodeTime = "";
	
	public static String modifyUserInfoDay = "";
	
	public static String appid = "";
	
	public static String privatekey = "";
	
	public static String publickey = "";
	
	public static String gatewayUrl = "";
	
	public static String signtype = "";
	
	public static String returnUrl = "";
	
	public static String notifyUrl = "";
	
	public static String timeoutExpress = "";
	
	public static String pageCount = "";
	
	public static void init()
	{
		jdbcUrl = PropKit.get("jdbcUrl");
		username = PropKit.get("username");
		password = PropKit.get("password");
		servId = PropKit.get("servId");
		servAuth = PropKit.get("servAuth");
		smsUrl = PropKit.get("smsUrl");
		mailHost = PropKit.get("mailHost");
		mailAuth = PropKit.get("mailAuth");
		mailForm = PropKit.get("mailForm");
		mailUsername = PropKit.get("mailUsername");
		mailPassword = PropKit.get("mailPassword");
		mailTransport = PropKit.get("mailTransport");
		sendEmail = PropKit.get("sendEmail");
		sendSms = PropKit.get("sendSms");
		emailChangeUrl = PropKit.get("emailChangeUrl");
		forgetPasswordCount = PropKit.get("forgetPasswordCount");
		forgetPasswordTime = PropKit.get("forgetPasswordTime");
		modifyEmailPhoneCount = PropKit.get("modifyEmailPhoneCount");
		modifyPhoneTime = PropKit.get("modifyPhoneTime");
		loginErrorCount = PropKit.get("loginErrorCount");
		userAddSendPhoneCount = PropKit.get("userAddSendPhoneCount");
		loginErrorTime = PropKit.get("loginErrorTime");
		userAddSendPhoneTime = PropKit.get("userAddSendPhoneTime");
		userAddCodeTime = PropKit.get("userAddCodeTime");
		modifyUserInfoDay = PropKit.get("modifyUserInfoDay");
		appid = PropKit.get("appid");
		gatewayUrl = PropKit.get("gatewayUrl");
		signtype = PropKit.get("signtype");
		returnUrl = PropKit.get("returnUrl");
		notifyUrl = PropKit.get("notifyUrl");
		timeoutExpress = PropKit.get("timeoutExpress");
		pageCount = PropKit.get("pageCount");
		
		try
		{
			BufferedReader br_private = new BufferedReader(new InputStreamReader(Config.class.getResourceAsStream("/privatekey.txt")));
			BufferedReader br_public = new BufferedReader(new InputStreamReader(Config.class.getResourceAsStream("/publickey.txt")));
			privatekey  = br_private.readLine();
			publickey  = br_public.readLine();
			br_private.close();
			br_public.close();
			System.out.println("privatekey:"+privatekey);
			System.out.println("publickey:"+publickey);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//session变量名称
	public static String sessionUserid = "userid";
	public static String sessionPhone = "phone";
	public static String sessionPhoneResetCode = "phoneResetCode";
}