package com.dbkj.account.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dbkj.account.interceptor.AdminAuthorityTemplateDirectiveInterceptor;
import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.interceptor.LoginInterceptor;
import com.dbkj.account.interceptor.ResetPasswordInterceptor;
import com.dbkj.account.interceptor.UserAuthorityTemplateDirectiveInterceptor;
import com.dbkj.account.sys.form.UserAddTimeOutForm;
import com.dbkj.account.sys.form.UserEmailForm;
import com.dbkj.account.sys.form.UserForgetPasswordForm;
import com.dbkj.account.sys.form.UserLoginForm;
import com.dbkj.account.sys.form.UserPhoneForm;
import com.dbkj.account.sys.form.VoiceForm;
import com.dbkj.account.sys.inter.LoginInter;
import com.dbkj.account.sys.util.Md5Util;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

@Clear({LoginInterceptor.class,AuthInterceptor.class,AdminAuthorityTemplateDirectiveInterceptor.class,UserAuthorityTemplateDirectiveInterceptor.class,ResetPasswordInterceptor.class})
public class UserController extends Controller
{
	public void index()
	{
		render("user/userlogin.html");
	}
	
	public void timeout()
	{
		render("user/userlogintimeout.html");
	}
	
	public void useradd()
	{
		setAttr("count",Config.userAddSendPhoneCount);
		setAttr("time",Config.userAddSendPhoneTime);
		render("user/useradd.html");
	}
	
	public void userloginOk()
	{
		String phone = getPara("phone");
		String type = getPara("type");
		String sql = "";
		if(type.equals("phone"))
		{
			sql = "select id,phone from user where phone='"+phone+"'";
		}
		else if(type.equals("email"))
		{
			sql = "select id,phone from user where email='"+phone+"'";
		}
		Record record = Db.findFirst(sql);
		String userid = record.getStr("id");
		String _phone = record.getStr("phone");
		getSession().setAttribute(Config.sessionPhone,_phone);
		getSession().setAttribute(Config.sessionUserid,userid);
		//render("user/userindex.html");
		setAttr("count",Config.modifyEmailPhoneCount);
		render("new/index.html");
	}
	
	//测试下载文件
	//public void getFileTest()
	//{
	//	renderFile(new File("D:\\uploadimages\\54757bf553b1f.jpg"),"aaaaa.jpg");
	//}

	public void findpassword()
	{
		setAttr("count",Config.forgetPasswordCount);
		setAttr("time",Config.forgetPasswordTime);
		render("user/findpassword.html");
	}
	
	public void forgetPassword()
	{
		String phone = getPara("phone");
		String code = getPara("code");
		String type = getPara("type");
		if(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYFORGET)==null)
		{
			renderText("imagetime");
			return;
		}
		if(!code.toUpperCase().equals(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYFORGET).toString().toUpperCase()))
		{
			renderText("image");
			return;
		}
		
		if(type.equals("phone"))
		{
			String sql = "select id from user where phone='"+phone+"'";
			Record record = Db.findFirst(sql);
			if(record==null)
			{
				renderText("no");
				return;
			}
		}
		else if(type.equals("email"))
		{
			String sql = "select id from user where email='"+phone+"'";
			Record record = Db.findFirst(sql);
			if(record==null)
			{
				renderText("no");
				return;
			}
		}
		
		String ip = getRequest().getRemoteAddr();
		System.out.println("ip:"+ip);
		if(MapValue.map_user_forget.get(ip)!=null)
		{
			UserForgetPasswordForm f = MapValue.map_user_forget.get(ip);
			int trycount = f.getTrycount();
			if(trycount>=Integer.parseInt(Config.forgetPasswordCount))
			{
				renderText("more");
				return;
			}
			else
			{
				trycount++;
				f.setTrycount(trycount);
				MapValue.map_user_forget.put(ip,f);
			}
		}
		else
		{
			UserForgetPasswordForm f = new UserForgetPasswordForm();
			f.setDate(new Date());
			f.setTrycount(1);
			MapValue.map_user_forget.put(ip,f);
		}
		
		if(type.equals("phone"))
		{
			String randomcode = getRanPassword(4);
			PhoneUtil.sendSms(randomcode,phone,1);
			System.out.println("手机密码："+randomcode);
			String sql = "update user set password='"+Md5Util.getMd5Encode(randomcode)+"' where phone='"+phone+"'";
			Db.update(sql);
			MapValue.map_login.remove(phone);
		}
		else if(type.equals("email"))
		{
			String randomcode = getRanPassword(6);
			EmailUtil.sendEmailPassword(randomcode,phone);
			System.out.println("邮箱密码："+randomcode);
			String sql = "update user set password='"+Md5Util.getMd5Encode(randomcode)+"' where email='"+phone+"'";
			Db.update(sql);
			sql = "select phone from user where email='"+phone+"'";
			Record record = Db.findFirst(sql);
			String ph = record.getStr("phone");
			MapValue.map_login.remove(ph);
		}
		renderText("ok");
	}
	
	public void randomImageEmail()
	{
		try
		{
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			response.setContentType("image/jpeg");
			response.setHeader("Pragma","No-cache");
			response.setHeader("Cache-Control","no-cache");
			response.setDateHeader("Expire",0);
			CreateRandomImageUtil random = new CreateRandomImageUtil();
			random.getRandcodeEmail(request,response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	
	public void randomImageForget()
	{
		try
		{
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			response.setContentType("image/jpeg");
			response.setHeader("Pragma","No-cache");
			response.setHeader("Cache-Control","no-cache");
			response.setDateHeader("Expire",0);
			CreateRandomImageUtil random = new CreateRandomImageUtil();
			random.getRandcodeForget(request,response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	
	public void randomImageUserAdd()
	{
		try
		{
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			response.setContentType("image/jpeg");
			response.setHeader("Pragma","No-cache");
			response.setHeader("Cache-Control","no-cache");
			response.setDateHeader("Expire",0);
			CreateRandomImageUtil random = new CreateRandomImageUtil();
			random.getRandcodeUserAdd(request,response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	
	public void randomImagePhone()
	{
		try
		{
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			response.setContentType("image/jpeg");
			response.setHeader("Pragma","No-cache");
			response.setHeader("Cache-Control","no-cache");
			response.setDateHeader("Expire",0);
			CreateRandomImageUtil random = new CreateRandomImageUtil();
			random.getRandcodePhone(request,response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	
	/*
	public void userEmailUpdate()
	{
		String email = getPara("email");
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String sql = "update user set email='"+email+"' where id="+userid;
		Db.update(sql);
		render("user/useremailupdateok.html");
	}
	*/
	
	public void userProfileUpdate()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String username = getPara("username");
		String usernamehas = getPara("usernamehas");
		String name = getPara("name");
		
		String sql = "update user set name='"+name+"'";
		if(usernamehas==null)
		{
			sql += ",username='"+username+"'";
		}
		sql += " where id="+userid;
		Db.update(sql);
		render("user/userprofileupdateok.html");
	}
	
	public void editprofile()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String phone = getSession().getAttribute(Config.sessionPhone).toString();
		String sql = "select username,name,email from user where id="+userid;
		Record record = Db.findFirst(sql);
		setAttr("username",record.getStr("username")==null?"":record.getStr("username"));
		setAttr("name",record.getStr("name")==null?"":record.getStr("name"));
		String email = record.getStr("email")==null?"":record.getStr("email");
		if(email.equals(""))
		{
			email = "<span style='color:red;'>未绑定邮箱</span>";
		}
		setAttr("email",email);
		setAttr("phone",phone);
		sql = "select email from user_info_reset where userid="+userid;
		record = Db.findFirst(sql);
		if(record!=null)
		{
			String e = record.getStr("email")==null?"":record.getStr("email");
			setAttr("emailreset",e);
		}
		else
		{
			setAttr("emailreset","");
		}
		render("user/usereditprofile.html");
	}
	
	public void voiceadd()
	{
		render("user/voiceadd.html");
	}
	
	public void deleteVoice()
	{
		String id = getPara("id");
		String sql = "delete from user_voice where id="+id;
		Db.update(sql);
		redirect(getRequest().getContextPath()+"/user/listVoice");
	}
	
	public void userEmailFindPassword()
	{
		String email = getPara("email");
		String password = getRanPassword(6);
		System.out.println("密码："+password);
		String sql = "update user set password='"+Md5Util.getMd5Encode(password)+"' where email='"+email+"'";
		Db.update(sql);
		EmailUtil.sendEmail(password,email);
		render("user/userpasswordemail.html");
	}
	
	public void userPhoneFindPassword()
	{
		String phone = getPara("phone");
		String password = getRanPassword(6);
		System.out.println("密码："+password);
		String sql = "update user set password='"+Md5Util.getMd5Encode(password)+"' where phone='"+phone+"'";
		Db.update(sql);
		PhoneUtil.sendSms(password,phone,1);
		MapValue.map_login.remove(phone);
		render("user/userpassword.html");
	}
	
	public void checkEmail()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String email = getPara("email");
		String sql = "select id from user where id="+userid + " and email='"+email+"'";
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			renderText("self");
		}
		else
		{
			sql = "select id from user where email='"+email+"'";
			record = Db.findFirst(sql);
			if(record==null)
			{
				renderText("no");
			}
			else
			{
				renderText("yes");
			}
		}
	}
	
	public void testSendResetEmail()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String vertifyCode = getPara("vertifyCode");
		if(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYEMAIL)==null)
		{
			renderText("imagetime");
			return;
		}
		if(!vertifyCode.toUpperCase().equals(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYEMAIL).toString().toUpperCase()))
		{
			renderText("no");
			return;
		}
		String email = getPara("email");
		String random = getRanCodeEmailReset(6);
		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		SimpleDateFormat sf_date = new SimpleDateFormat("yyyy-MM-dd");
		String time = sf.format(date);
		String date_time = sf_date.format(date);
		String urlstring = userid + email + random + time;
		urlstring = Md5Util.getMd5Encode(urlstring);
		urlstring += Md5Util.getMd5Encode(urlstring);
		
		String sql = "select id,date_format(time,'%Y-%m-%d') as date,trycount from user_info_reset where userid="+userid;
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			int id = record.getInt("id");
			String _date = record.getStr("date");
			int trycount = record.getInt("trycount");
			if(date_time.compareTo(_date)<=0)
			{
				if(trycount>=Integer.parseInt(Config.modifyEmailPhoneCount))
				{
					renderText("more");
				}
				else
				{
					trycount++;
					sql = "update user_info_reset set urlstring='"+urlstring+"',time=now(),email='"+email+"',trycount="+trycount+",status=0,emailtime=now() where id="+id;
					Db.update(sql);
					renderText("ok");
				}
			}
			else
			{
				sql = "update user_info_reset set urlstring='"+urlstring+"',time=now(),email='"+email+"',trycount=1,status=0,emailtime=now() where id="+id;
				Db.update(sql);
				renderText("ok");
			}
		}
		else
		{
			sql = "insert into user_info_reset (urlstring,userid,time,email,trycount,status,emailtime) values ('"+urlstring+"',"+userid+",now(),'"+email+"',1,0,now())";
			Db.update(sql);
			renderText("ok");
		}
	}
	
	public void SendResetPhoneOk()
	{
		try
		{
			String code = getPara("code");
			String userid = getSession().getAttribute(Config.sessionUserid).toString();
			if(getRequest().getSession().getAttribute(Config.sessionPhoneResetCode)==null)
			{
				renderText("codetime");
				return;	
			}
			if(!code.equals(getRequest().getSession().getAttribute(Config.sessionPhoneResetCode).toString()))
			{
				renderText("no");
				return;
			}
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sql = "select date_format(phonetime,'%Y-%m-%d %H:%i:%s') as time from user_info_reset where userid="+userid;
			Record record = Db.findFirst(sql);
			String time = record.getStr("time");
			Date date_phone = sf.parse(time);
			if((date.getTime()-date_phone.getTime())>1000*60*Integer.parseInt(Config.modifyPhoneTime))
			{
				renderText("timeout");
				return;
			}
			String phone = getPara("phone");
			sql = "update user set phone='"+phone+"' where id="+userid;
			Db.update(sql);
			sql = "update user_info_reset set status=1 where userid="+userid;
			Db.update(sql);
			renderText("ok");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			renderText("error");
		}
	}
	 
	public void testSendResetPhone()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String vertifyCode = getPara("vertifyCode");
		if(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYPHONE)==null)
		{
			renderText("timeout");
			return;
		}
		if(!vertifyCode.toUpperCase().equals(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYPHONE).toString().toUpperCase()))
		{
			renderText("no");
			return;
		}
		String phone = getPara("phone");
		String random = getRanCodePhone(4);
		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		SimpleDateFormat sf_date = new SimpleDateFormat("yyyy-MM-dd");
		String time = sf.format(date);
		String date_time = sf_date.format(date);
		String urlstring = userid + phone + random + time;
		urlstring = Md5Util.getMd5Encode(urlstring);
		urlstring += Md5Util.getMd5Encode(urlstring);
		
		String sql = "select id,date_format(time,'%Y-%m-%d') as date,trycount from user_info_reset where userid="+userid;
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			int id = record.getInt("id");
			String _date = record.getStr("date");
			int trycount = record.getInt("trycount");
			if(date_time.compareTo(_date)<=0)
			{
				if(trycount>=Integer.parseInt(Config.modifyEmailPhoneCount))
				{
					renderText("more");
				}
				else
				{
					trycount++;
					sql = "update user_info_reset set urlstring='"+urlstring+"',time=now(),phone='"+phone+"',trycount="+trycount+",status=0,phonetime=now() where id="+id;
					Db.update(sql);
					renderText("ok");
				}
			}
			else
			{
				sql = "update user_info_reset set urlstring='"+urlstring+"',time=now(),phone='"+phone+"',trycount=1,status=0,phonetime=now() where id="+id;
				Db.update(sql);
				renderText("ok");
			}
		}
		else
		{
			sql = "insert into user_info_reset (urlstring,userid,time,phone,trycount,status,phonetime) values ('"+urlstring+"',"+userid+",now(),'"+phone+"',1,0,now())";
			Db.update(sql);
			renderText("ok");
		}
	}
	
	public void SendResetEmail()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String vertifyCode = getPara("vertifyCode");
		if(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYEMAIL)==null)
		{
			renderText("timeout");
			return;
		}
		if(!vertifyCode.toUpperCase().equals(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYEMAIL).toString().toUpperCase()))
		{
			renderText("no");
			return;
		}
		String sql = "select urlstring,email from user_info_reset where userid="+userid;
		Record record = Db.findFirst(sql);
		String urlstring = record.getStr("urlstring");
		String email = record.getStr("email");
		urlstring = Config.emailChangeUrl + "?id="+urlstring;
		System.out.println("urlstring:"+urlstring);
		EmailUtil.sendEmailHtml(urlstring,email);
		renderText("ok");
	}
	
	public void SendResetPhone()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String vertifyCode = getPara("vertifyCode");
		if(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYPHONE)==null)
		{
			renderText("timeout");
			return;
		}
		if(!vertifyCode.toUpperCase().equals(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYPHONE).toString().toUpperCase()))
		{
			renderText("no");
			return;
		}
		String sql = "select phone from user_info_reset where userid="+userid;
		Record record = Db.findFirst(sql);
		String phone = record.getStr("phone");
		String code = getRanCodePhone(4);
		System.out.println("phone:"+phone);
		System.out.println("code:"+code);
		getSession().setAttribute(Config.sessionPhoneResetCode,code);
		PhoneUtil.sendSms(code,phone,0);
		renderText("ok");
	}
	
	public void changeEmailLink()
	{
		//String userid = getRequest().getSession().getAttribute(Config.sessionUserid).toString();
		String id = getPara("id");
		String sql = "select email,userid from user_info_reset where urlstring='"+id+"'";
		Record record = Db.findFirst(sql);
		if(record==null)
		{
			setAttr("result","no");
			render("user/useremailresetok.html");
		}
		else
		{
			String userid = record.getStr("userid");
			String email = record.getStr("email");
			System.out.println("email:"+email);
			sql = "update user set email='"+email+"' where id="+userid;
			Db.update(sql);
			//sql = "delete from user_info_reset where userid="+userid;
			sql = "update user_info_reset set status=1 where userid="+userid;
			Db.update(sql);
			setAttr("result","ok");
			render("user/useremailresetok.html");
		}
	}
	
	public void checkUserName()
	{
		String username = getPara("username");
		String sql = "select id from user where username='"+username+"'";
		Record record = Db.findFirst(sql);
		if(record==null)
		{
			renderText("no");
		}
		else
		{
			renderText("yes");
		}
	}
	
	public void editVoice()
	{
		String id = getPara("id");
		String sql = "select date_format(uploadtime,'%Y-%m-%d %H:%i:%s') as uploadtime,filename,filepath,voicename,content,status from user_voice where id="+id;
		Record record = Db.findFirst(sql);
		setAttr("id",id);
		setAttr("uploadtime",record.getStr("uploadtime"));
		setAttr("filename",record.getStr("filename"));
		setAttr("filepath",record.getStr("filepath"));
		setAttr("voicename",record.getStr("voicename"));
		setAttr("content",record.getStr("content"));
		
		String status = record.getStr("status");
		setAttr("status",status);
		String status_text = "";
		if(status.equals("-1"))
		{
			status_text = "待提交审核";
		}
		else if(status.equals("0"))
		{
			status_text = "审核中";
		}
		else if(status.equals("1"))
		{
			status_text = "<span style='color:blue;'>审核通过</span>";
		}
		else if(status.equals("2"))
		{
			status_text = "<span style='color:red;'>审核不通过</span>";
		}
		setAttr("status_text",status_text);
		
		render("user/voiceedit.html");
	}
	
	public void viewVoice()
	{
		String id = getPara("id");
		String sql = "select date_format(uploadtime,'%Y-%m-%d %H:%i:%s') as uploadtime,filename,filepath,voicename,content,status from user_voice where id="+id;
		Record record = Db.findFirst(sql);
		setAttr("id",id);
		setAttr("uploadtime",record.getStr("uploadtime"));
		setAttr("filename",record.getStr("filename"));
		setAttr("filepath",record.getStr("filepath"));
		setAttr("voicename",record.getStr("voicename"));
		setAttr("content",record.getStr("content"));
		
		String status = record.getStr("status");
		setAttr("status",status);
		String status_text = "";
		if(status.equals("-1"))
		{
			status_text = "待提交审核";
		}
		else if(status.equals("0"))
		{
			status_text = "审核中";
		}
		else if(status.equals("1"))
		{
			status_text = "<span style='color:blue;'>审核通过</span>";
		}
		else if(status.equals("2"))
		{
			status_text = "<span style='color:red;'>审核不通过</span>";
		}
		setAttr("status_text",status_text);
		
		render("user/voiceview.html");
	}
	
	public void userAddCheckUserName()
	{
		String username = getPara("username");
		String sql = "select id from user where username='"+username+"'";
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			renderText("has");
		}
		else
		{
			renderText("no");
		}
	}
	
	public void userAddCheckPhone()
	{
		String phone = getPara("phone");
		String sql = "select id from user where phone='"+phone+"'";
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			renderText("has");
		}
		else
		{
			renderText("no");
		}
	}

	public void userAddCheckCode()
	{
		String code = getPara("code");
		if(!code.toUpperCase().equals(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYUSERADD).toString().toUpperCase()))
		{
			renderText("no");
		}
		else
		{
			renderText("ok");
		}
	}
	
	public void listVoice()
	{
		List<VoiceForm> listVoice = new ArrayList<VoiceForm>();
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String sql = "select id,userid,date_format(uploadtime,'%Y-%m-%d %H:%i:%s') as uploadtime,filename,voicename,status from user_voice where userid="+userid+" order by status,uploadtime desc";
		List<Record> list = Db.find(sql);
		for(int i=0;i<list.size();i++)
		{
			Record r = list.get(i);
			VoiceForm f = new VoiceForm();
			f.setNo((i+1)+"");
			f.setId(r.getStr("id"));
			f.setUserid(r.getStr("userid"));
			f.setUploadtime(r.getStr("uploadtime"));
			f.setFilename(r.getStr("filename"));
			f.setVoicename(r.getStr("voicename"));
			f.setStatus(r.getStr("status"));
			String status_text = "";
			if(f.getStatus().equals("-1"))
			{
				status_text = "待提交审核";
			}
			else if(f.getStatus().equals("0"))
			{
				status_text = "审核中";
			}
			else if(f.getStatus().equals("1"))
			{
				status_text = "<span style='color:blue;'>审核通过</span>";
			}
			else if(f.getStatus().equals("2"))
			{
				status_text = "<span style='color:red;'>审核不通过</span>";
			}
			f.setStatus_text(status_text);
			listVoice.add(f);
		}
		setAttr("listVoice",listVoice);
		render("user/listvoice.html");
	}
	
	public void userInfoEnter()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String companyname = getPara("companyname");
		String contact = getPara("contact");
		String contactphone = getPara("contactphone");
		String accountname = getPara("accountname");
		String publicaccount = getPara("publicaccount");
		String bank = getPara("bank");
		String taxaccount = getPara("taxaccount");
		String licence = getPara("licence");
		String idcard = getPara("idcard");
		String safety = getPara("safety");
		String test = "select id from user_info where userid="+userid;
		Record record = Db.findFirst(test);
		if(record==null)
		{
			String sql = "insert into user_info (userid,companyname,contact,contactphone,publicaccount,licence,idcard,safety,ispass,create_time,modify_time,accountname,bank,taxaccount) values ("+userid+",'"+companyname+"','"+contact+"','"+contactphone+"','"+publicaccount+"','"+licence+"','"+idcard+"','"+safety+"',0,now(),now(),'"+accountname+"','"+bank+"','"+taxaccount+"')";
			Db.update(sql);
		}
		else
		{
			String sql = "update user_info set companyname='"+companyname+"',contact='"+contact+"',contactphone='"+contactphone+"',publicaccount='"+publicaccount+"',licence='"+licence+"',idcard='"+idcard+"',safety='"+safety+"',ispass=0,modify_time=now(),accountname='"+accountname+"',bank='"+bank+"',taxaccount='"+taxaccount+"' where userid="+userid;
			Db.update(sql);
		}
		render("user/userinfoupdateok.html");
	}
	
	public void voiceInfoEnter()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String filename = getPara("filename");
		String filepath = getPara("voice");
		String voicename = getPara("voicename");
		String content = getPara("content");
		String audit = getPara("audit");
		String status = "-1";
		if(audit.equals("yes"))
		{
			status = "0";
		}
		String sql = "insert into user_voice (userid,uploadtime,filename,filepath,voicename,content,status) values ("+userid+",now(),'"+filename+"','"+filepath+"','"+voicename+"','"+content+"',"+status+")";
		Db.update(sql);
		redirect(getRequest().getContextPath()+"/user/listVoice");
	}
	
	public void voiceInfoUpdate()
	{
		//String userid = getSession().getAttribute("userid").toString();
		String id = getPara("id");
		String filename = getPara("filename");
		String filepath = getPara("voice");
		String voicename = getPara("voicename");
		String content = getPara("content");
		String audit = getPara("audit");
		String sql = "update user_voice set uploadtime=now(),filename='"+filename+"',filepath='"+filepath+"',voicename='"+voicename+"',content='"+content+"'";
		if(audit.equals("yes"))
		{
			sql += ",status=0";
		}
		sql += " where id="+id;
		Db.update(sql);
		redirect(getRequest().getContextPath()+"/user/listVoice");
	}
	
	public void uploadFileVoice()
	{
		UploadFile uploadFile = getFile();
		File filesrc = uploadFile.getFile();
		String phone = getSession().getAttribute(Config.sessionPhone).toString();
		String path = getSession().getServletContext().getRealPath("/uploadvoices/"+phone);
		File folder = new File(path);
		if(!folder.exists())
		{
			folder.mkdirs();
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String filename = sf.format(new Date()) + "_" + phone + ".wav";
		path = getSession().getServletContext().getRealPath("/uploadvoices/"+phone+"/"+filename);
		System.out.println(path);
		File filedest = new File(path);
		try
		{
			FileInputStream fis = new FileInputStream(filesrc);
			FileOutputStream fos = new FileOutputStream(filedest);
			byte bs[] = new byte[1024*1024];
			int read = -1;
			while((read=fis.read(bs))!=-1)
			{
				fos.write(bs,0,read);
			}
			fos.close();
			fis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		setAttr("filepath",phone+"/"+filename);
		render("user/uploadvoiceok.html");
	}
	
	public void uploadFile()
	{
		UploadFile uploadFile = getFile();
		String name = uploadFile.getOriginalFileName();
		int pos = name.lastIndexOf(".");
		String ext = name.substring(pos+1);
		File filesrc = uploadFile.getFile();
		String type = getPara("type");
		String phone = getSession().getAttribute(Config.sessionPhone).toString();
		String path = getSession().getServletContext().getRealPath("/uploadimages/"+phone);
		File folder = new File(path);
		if(!folder.exists())
		{
			folder.mkdirs();
		}
		path = getSession().getServletContext().getRealPath("/uploadimages/"+phone+"/"+type+"."+ext);
		System.out.println(path);
		File filedest = new File(path);
		try
		{
			FileInputStream fis = new FileInputStream(filesrc);
			FileOutputStream fos = new FileOutputStream(filedest);
			byte bs[] = new byte[1024*1024];
			int read = -1;
			while((read=fis.read(bs))!=-1)
			{
				fos.write(bs,0,read);
			}
			fos.close();
			fis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		setAttr("type",type);
		setAttr("filepath",phone+"/"+type+"."+ext);
		render("user/uploadok.html");
	}
	
	public void showImage()
	{
		try
		{
			String filepath = getPara("filepath");
			String path = getSession().getServletContext().getRealPath("/uploadimages/"+filepath);
			FileInputStream fis = new FileInputStream(new File(path));
			OutputStream os = getResponse().getOutputStream();
			byte bs[] = new byte[1024*1024];
			int read = -1;
			while((read=fis.read(bs))!=-1)
			{
				os.write(bs,0,read);
			}
			os.close();
			fis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	
	public void userinfo()
	{
		String companyname = "";
		String contact = "";
		String contactphone = "";
		String accountname = "";
		String publicaccount = "";
		String bank = "";
		String taxaccount = "";
		String licence = "";
		String idcard = "";
		String safety = "";
		String ispass = "";
		String ispass_text = "";
		String remark = "";
		
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String phone = getSession().getAttribute(Config.sessionPhone).toString();
		String sql = "select companyname,contact,contactphone,publicaccount,licence,idcard,safety,ispass,accountname,bank,taxaccount,remark from user_info where userid="+userid;
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			companyname = record.getStr("companyname");
			contact = record.getStr("contact");
			contactphone = record.getStr("contactphone");
			accountname = record.getStr("accountname");
			publicaccount = record.getStr("publicaccount");
			bank = record.getStr("bank");
			taxaccount = record.getStr("taxaccount");
			licence = record.getStr("licence");
			idcard = record.getStr("idcard");
			safety = record.getStr("safety");
			ispass = record.getStr("ispass");
			remark = record.getStr("remark")==null?"":record.getStr("remark");
			remark = remark.replace("\n","<br>");
		}

		if(ispass.equals("0"))
		{
			ispass_text = "审核中";
		}
		else if(ispass.equals("1"))
		{
			ispass_text = "<span style='color:blue;'>审核通过</span>";
		}
		else if(ispass.equals("2"))
		{
			ispass_text = "<span style='color:red;'>审核不通过</span>";
		}

		setAttr("companyname",companyname);
		setAttr("contact",contact);
		setAttr("contactphone",contactphone);
		setAttr("accountname",accountname);
		setAttr("publicaccount",publicaccount);
		setAttr("bank",bank);
		setAttr("taxaccount",taxaccount);
		setAttr("licence",licence);
		setAttr("idcard",idcard);
		setAttr("safety",safety);
		setAttr("ispass",ispass);
		setAttr("ispass_text",ispass_text);
		setAttr("remark",remark);
		
		setAttr("phone",phone);
		
		render("user/userinfo.html");
	}
	
	public void userEditPassword()
	{
		String phone = getSession().getAttribute(Config.sessionPhone).toString();
		String password = getPara("new_password");
		String sql = "update user set password='"+Md5Util.getMd5Encode(password)+"' where phone='"+phone+"'";
		Db.update(sql);
		renderNull();
	}
	
	public void checkSrcPassword()
	{
		String phone = getSession().getAttribute(Config.sessionPhone).toString();
		String src_password = getPara("src_password");
		String sql = "select id from user where phone='"+phone+"' and password='"+Md5Util.getMd5Encode(src_password)+"'";
		Record record = Db.findFirst(sql);
		if(record==null)
		{
			renderText("no");
		}
		else
		{
			renderText("ok");
		}
	}
	
	public void editpassword()
	{
		String phone = getSession().getAttribute(Config.sessionPhone).toString();
		setAttr("phone",phone);
		render("user/usereditpassword.html");
	}
	
	public void checkLogin()
	{
		try
		{
			String phone = getPara("phone");
			String password = getPara("password");
			String type = getPara("type");
			String email = "";
			if(type.equals("email"))
			{
				String sql = "select phone from user where email='"+phone+"'";
				Record record = Db.findFirst(sql);
				email = record.getStr("phone");
			}
			UserLoginForm login = null;
			if(type.equals("phone"))
			{
				login = MapValue.map_login.get(phone);
			}
			else if(type.equals("email"))
			{
				login = MapValue.map_login.get(email);
			}
			if(login!=null)
			{
				int count = login.getCount();
				if(count>Integer.parseInt(Config.loginErrorCount))
				{
					renderText("more");
					return;
				}
			}
			
			String sql = "";
			if(type.equals("phone"))
			{
				sql = "select id from user where phone='"+phone+"' and password='"+Md5Util.getMd5Encode(password)+"'";
			}
			else if(type.equals("email"))
			{
				sql = "select id from user where email='"+phone+"' and password='"+Md5Util.getMd5Encode(password)+"'";
			}
			Record record = Db.findFirst(sql);
			if(record==null)
			{
				String s = "";
				if(type.equals("phone"))
				{
					s = "select id from user where phone='"+phone+"'";
				}
				else if(type.equals("email"))
				{
					s = "select id from user where email='"+phone+"'";
				}
				Record r = Db.findFirst(s);
				if(r!=null)
				{
					if(type.equals("phone"))
					{
						if(MapValue.map_login.get(phone)==null)
						{
							UserLoginForm u = new UserLoginForm();
							u.setCount(1);
							u.setDate(new Date());
							MapValue.map_login.put(phone,u);
							renderText("nocount:"+Config.loginErrorCount);
						}
						else
						{
							int count = MapValue.map_login.get(phone).getCount();
							count++;
							MapValue.map_login.get(phone).setCount(count);
							if(count>3)
							{
								renderText("more");
							}
							else
							{
								renderText("nocount:"+(Integer.parseInt(Config.loginErrorCount)+1-count));
							}
						}
					}
					else if(type.equals("email"))
					{
						if(MapValue.map_login.get(email)==null)
						{
							UserLoginForm u = new UserLoginForm();
							u.setCount(1);
							u.setDate(new Date());
							MapValue.map_login.put(email,u);
							renderText("nocount:"+Config.loginErrorCount);
						}
						else
						{
							int count = MapValue.map_login.get(email).getCount();
							count++;
							MapValue.map_login.get(email).setCount(count);
							if(count>3)
							{
								renderText("more");
							}
							else
							{
								renderText("nocount:"+(Integer.parseInt(Config.loginErrorCount)+1-count));
							}
						}
					}
				}
				else
				{
					renderText("no");
				}
			}
			else
			{
				if(type.equals("phone"))
				{
					MapValue.map_login.remove(phone);
					renderText("ok");
				}
				else if(type.equals("email"))
				{
					MapValue.map_login.remove(email);
					renderText("ok");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void useraddok()
	{
		try
		{
			String code = getPara("code");
			String phone = getPara("phone");
			String username = getPara("username");
			String vcode = getPara("vcode");
			String sql = "select id from user where phone='"+phone+"'";
			Record record = Db.findFirst(sql);
			if(record!=null)
			{
				renderText("phone");
				return;
			}
			sql = "select id from user where username='"+username+"'";
			record = Db.findFirst(sql);
			if(record!=null)
			{
				renderText("username");
				return;
			}
			if(MapValue.map_phone.get(phone)==null)
			{
				renderText("timeout");
				return;
			}
			String _code = MapValue.map_phone.get(phone).getCode();
			if(!code.equals(_code))
			{
				renderText("code");
				return;
			}
			if(!vcode.toUpperCase().equals(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYUSERADD).toString().toUpperCase()))
			{
				renderText("image");
				return;
			}
			String password = getPara("password");
			sql = "insert into user (phone,username,password,create_time) values ('"+phone+"','"+username+"','"+Md5Util.getMd5Encode(password)+"',now())";
			Db.update(sql);
			getSession().setAttribute(Config.sessionPhone,phone);
			
			sql = "select id from user where phone='"+phone+"'";
			record = Db.findFirst(sql);
			String userid = record.getStr("id");
			getSession().setAttribute(Config.sessionUserid,userid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			renderText("error");
		}
		renderText("ok");
	}
	
	//用户注册短信验证
	public void sendsms()
	{
		String code = getPara("code");
		if(!code.toUpperCase().equals(getRequest().getSession().getAttribute(CreateRandomImageUtil.RANDOMCODEKEYUSERADD).toString().toUpperCase()))
		{
			renderText("image");
			return;
		}
		String ip = getRequest().getRemoteAddr();
		System.out.println("ip:"+ip);
		if(MapValue.map_user_add.get(ip)!=null)
		{
			UserAddTimeOutForm f = MapValue.map_user_add.get(ip);
			int trycount = f.getTrycount();
			if(trycount>=Integer.parseInt(Config.userAddSendPhoneCount))
			{
				renderText("more");
				return;
			}
			else
			{
				trycount++;
				f.setTrycount(trycount);
				MapValue.map_user_add.put(ip,f);
			}
		}
		else
		{
			UserAddTimeOutForm f = new UserAddTimeOutForm();
			f.setDate(new Date());
			f.setTrycount(1);
			MapValue.map_user_add.put(ip,f);
		}
		String phone = getPara("phone");
		System.out.println("发送短信的手机号："+phone);
		try
		{
			String randomcode = getRanCodePhone(4);
			PhoneUtil.sendSms(randomcode,phone,0);
			
			System.out.println("短信验证码："+randomcode);
			
			UserPhoneForm f = new UserPhoneForm();
			f.setCode(randomcode);
			f.setDate(new Date());
			MapValue.map_phone.put(phone,f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderText("ok");
	}
	
	//发送重置密码
	public void smsloginSend()
	{
		String phone = getPara("phone");
		System.out.println("发送短信的手机号："+phone);
		try
		{
			String password = getRanPassword(6);
			PhoneUtil.sendSms(password,phone,1);

			System.out.println("重置密码："+password);
			//String sql = "update user set password='"+password+"',trycount=0 where phone='"+phone+"'";
			String sql = "update user set password='"+Md5Util.getMd5Encode(password)+"' where phone='"+phone+"'";
			Db.update(sql);
			MapValue.map_login.remove(phone);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		render("user/userpassword.html");
	}
	
	public void checkPhone()
	{
//		String phone = getPara("phone");
//		System.out.println("检测的手机号："+phone);
//		String sql = "select id from user where phone='"+phone+"'";
//		Record record = Db.findFirst(sql);
//		if(record==null)
//		{
//			renderText("ok");
//		}
//		else
//		{
//			renderText("no");
//		}
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String phone = getPara("phone");
		System.out.println("检测的手机号："+phone);
		String sql = "select id from user where id="+userid + " and phone='"+phone+"'";
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			renderText("self");
		}
		else
		{
			sql = "select id from user where phone='"+phone+"'";
			record = Db.findFirst(sql);
			if(record==null)
			{
				renderText("no");
			}
			else
			{
				renderText("yes");
			}
		}
	}
	
	public String getRanCodeId(int len)
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
	
	public String getRanCodeEmailReset(int len)
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
	
	public String getRanCodeEmail(int len)
	{
		Random randGen = new Random();
		char[] numbersAndLetters = ("0123456789").toCharArray();
		char[] randBuffer = new char[len];
		for(int i=0;i<randBuffer.length;i++)
		{
			randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
		}
		return new String(randBuffer);
	}

	public String getRanCodePhone(int len)
	{
		Random randGen = new Random();
		char[] numbersAndLetters = ("0123456789").toCharArray();
		char[] randBuffer = new char[len];
		for(int i=0;i<randBuffer.length;i++)
		{
			randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
		}
		return new String(randBuffer);
	}
	
	public String getRanPassword(int len)
	{
		Random randGen = new Random();
		char[] numbersAndLetters = ("0123456789").toCharArray();
		char[] randBuffer = new char[len];
		for(int i=0;i<randBuffer.length;i++)
		{
			randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
		}
		return new String(randBuffer);
	}
}
