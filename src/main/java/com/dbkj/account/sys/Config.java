package com.dbkj.account.sys;

import com.jfinal.kit.PropKit;

public class Config
{
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
	
	static
	{
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
	}
}