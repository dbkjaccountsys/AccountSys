package com.dbkj.account.sys.inter;

import java.util.ArrayList;
import java.util.List;

import com.dbkj.account.sys.Config;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class LoginInter implements Interceptor
{
	//action过滤不需要拦截的
	//public static List<String> list_action_no = new ArrayList<String>();
	
	//action过滤需要拦截的
	public static List<String> list_action_yes = new ArrayList<String>();
	
	//ajax请求不需要拦截的
	//public static List<String> list_ajax_no = new ArrayList<String>();
	
	//ajax请求需要拦截的
	public static List<String> list_ajax_yes = new ArrayList<String>();
	
	static
	{
//		list_ajax_no.add("/user/forgetPassword");
//		list_ajax_no.add("/user/checkPhoneUserAdd");
//		list_ajax_no.add("/user/sendsms");
//		list_ajax_no.add("/user/useraddok");
//		list_ajax_no.add("/user/checkLogin");
//		list_ajax_no.add("/user/checkCode");
		
		list_ajax_yes.add("/user/checkSrcPassword");
		list_ajax_yes.add("/user/userEditPassword");
		list_ajax_yes.add("/user/checkUserName");
		list_ajax_yes.add("/user/checkEmail");
		list_ajax_yes.add("/user/testSendResetEmail");
		list_ajax_yes.add("/user/SendResetEmail");
		list_ajax_yes.add("/user/checkPhone");
		list_ajax_yes.add("/user/testSendResetPhone");
		list_ajax_yes.add("/user/SendResetPhone");
		list_ajax_yes.add("/user/SendResetPhoneOk");
		
		
//		list_action_no.add("/user");
//		list_action_no.add("/user/randomImageForget");
//		list_action_no.add("/user/showImage");
//		list_action_no.add("/user/randomImageUserAdd");
//		///user/uploadFile待定
//		list_action_no.add("/user/uploadFile");
//		list_action_no.add("/user/userloginOk");
//		list_action_no.add("/user/findpassword");
//		list_action_no.add("/user/useradd");
//		list_action_no.add("/user/smsLogin");
//		list_action_no.add("/user/smsloginSend");
//		///user/uploadFileVoice待定
//		list_action_no.add("/user/uploadFileVoice");
//		list_action_no.add("/user/timeout");
//		list_action_no.add("/user/randomImageEmail");
//		list_action_no.add("/user/randomImagePhone");
//		list_action_no.add("/user/changeEmailLink");
		
		//用户管理
		list_action_yes.add("/user/voiceadd");
		list_action_yes.add("/user/viewVoice");
		list_action_yes.add("/user/editVoice");
		list_action_yes.add("/user/deleteVoice");
		list_action_yes.add("/new");
		list_action_yes.add("/user/userProfileUpdate");
		list_action_yes.add("/user/userInfoEnter");
		list_action_yes.add("/user/voiceInfoEnter");
		list_action_yes.add("/user/voiceInfoUpdate");
		list_action_yes.add("/user/editprofile");
		list_action_yes.add("/user/editpassword");
		list_action_yes.add("/user/userinfo");
		list_action_yes.add("/user/listVoice");
		
		//财务管理
		list_action_yes.add("/money");
		list_action_yes.add("/money/addmoneystart");
		list_action_yes.add("/money/payReturn");
		list_action_yes.add("/money/payReturnOk");
		list_action_yes.add("/money/moneyRecordList");
	}
	
	public void intercept(Invocation inv)
	{
		Controller controller = inv.getController();
		Object obj = controller.getSessionAttr(Config.sessionUserid);
		
		System.out.println(inv.getActionKey());
		String key = inv.getActionKey();
		
		if(obj==null)
		{
			if(list_ajax_yes.contains(key))
			{
				controller.renderText("session");
				return;
			}
			if(list_action_yes.contains(key))
			{
				String ctx = controller.getRequest().getContextPath();
				//System.out.println(ctx);
				String str = "<script type='text/javascript'>top.location.href='"+ctx+"/user/timeout'</script>";
				controller.renderHtml(str);
				return;
			}
			inv.invoke();
		}
		else
		{
			inv.invoke();
		}
	}
}