package com.dbkj.account.sys;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil
{
	public static void sendEmail(final String code,final String mailto)
	{
		if(Config.sendEmail.equals("true"))
		{
			Thread thread = new Thread()
			{
				public void run()
				{
					try
					{
						Properties props = new Properties();
						props.put("mail.smtp.host",Config.mailHost);
						props.put("mail.smtp.auth",Config.mailAuth);
						Session session = Session.getDefaultInstance(props);
						//session.setDebug(true);
						MimeMessage message = new MimeMessage(session);
						message.setFrom(new InternetAddress(Config.mailForm));
						message.addRecipient(Message.RecipientType.TO,new InternetAddress(mailto));
						message.setSubject("邮件验证码");
						
						String content = "你好，你的邮箱验证码为："+code;
						message.setContent(content,"text/html;charset=utf-8");
						message.saveChanges();
						Transport transport = session.getTransport(Config.mailTransport);
						transport.connect(Config.mailHost,Config.mailUsername,Config.mailPassword);
						transport.sendMessage(message, message.getAllRecipients());
						transport.close();
						
						System.out.println("发送邮件成功");
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
}