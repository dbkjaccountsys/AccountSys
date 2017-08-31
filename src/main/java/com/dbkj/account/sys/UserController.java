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

import com.dbkj.account.sys.form.UserEmailForm;
import com.dbkj.account.sys.form.UserLoginForm;
import com.dbkj.account.sys.form.UserPhoneForm;
import com.dbkj.account.sys.form.VoiceForm;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

public class UserController extends Controller
{
	//是否发送短信，测试用
	public boolean sendsms = false;
	
	public void index()
	{
		render("user/userlogin.html");
	}
	
	public void useradd()
	{
		render("user/useradd.html");
	}
	
	public void userloginOk()
	{
		String phone = getPara("phone");
		getSession().setAttribute("phone",phone);
		String sql = "select id from user where phone='"+phone+"'";
		Record record = Db.findFirst(sql);
		String userid = record.getStr("id");
		getSession().setAttribute("userid",userid);
		render("user/userindex.html");
	}
	
	//测试下载文件
	//public void getFileTest()
	//{
	//	renderFile(new File("D:\\uploadimages\\54757bf553b1f.jpg"),"aaaaa.jpg");
	//}

	public void findpassword()
	{
		render("user/findpassword.html");
	}
	
	public void randomImage()
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
			random.getRandcode(request,response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	
	public void checkRandomCode()
	{
		String vertifyCode = getPara("vertifyCode");
		//System.out.println("========="+vertifyCode+"==========");
		if(vertifyCode.toUpperCase().equals(getRequest().getSession().getAttribute("sessionRandomCode").toString().toUpperCase()))
		{
			renderText("true");
		}
		else
		{
			renderText("false");
		}
	}
	
	public void userEmailUpdate()
	{
		String email = getPara("email");
		String userid = getSession().getAttribute("userid").toString();
		String sql = "update user set email='"+email+"' where id="+userid;
		Db.update(sql);
		render("user/useremailupdateok.html");
	}
	
	public void userProfileUpdate()
	{
		String userid = getSession().getAttribute("userid").toString();
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
		String userid = getSession().getAttribute("userid").toString();
		String phone = getSession().getAttribute("phone").toString();
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
		render("user/usereditprofile.html");
	}
	
	public void editemail()
	{
		String userid = getSession().getAttribute("userid").toString();
		String sql = "select email from user where id="+userid;
		Record record = Db.findFirst(sql);
		setAttr("email",record.getStr("email")==null?"":record.getStr("email"));
		render("user/usereditemail.html");
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
	
	public void checkEmailCode()
	{
		try
		{
			String phone = getSession().getAttribute("phone").toString();
			String code = getPara("code");
			UserEmailForm f = MapValue.map_email.get(phone);
			if(f==null)
			{
				renderText("no");
			}
			else
			{
				String c = f.getCode();
				if(code.equals(c))
				{
					renderText("ok");
				}
				else
				{
					renderText("no");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void checkEmailCodeForget()
	{
		try
		{
			String code = getPara("code");
			String email = getPara("email");
			String sql = "select phone from user where email='"+email+"'";
			Record record = Db.findFirst(sql);
			String phone = record.getStr("phone");
			UserEmailForm f = MapValue.map_email.get(phone);
			if(f==null)
			{
				renderText("no");
			}
			else
			{
				String c = f.getCode();
				if(code.equals(c))
				{
					renderText("ok");
				}
				else
				{
					renderText("no");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void checkSmsCodeForget()
	{
		try
		{
			String code = getPara("code");
			String phone = getPara("phone");
			UserPhoneForm f = MapValue.map_phone.get(phone);
			if(f==null)
			{
				renderText("no");
			}
			else
			{
				String c = f.getCode();
				if(code.equals(c))
				{
					renderText("ok");
				}
				else
				{
					renderText("no");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void userEmailFindPassword()
	{
		String email = getPara("email");
		String password = getRanPassword(6);
		System.out.println("密码："+password);
		String sql = "update user set password='"+password+"' where email='"+email+"'";
		Db.update(sql);
		EmailUtil.sendEmail(password,email);
		render("user/userpasswordemail.html");
	}
	
	public void userPhoneFindPassword()
	{
		String phone = getPara("phone");
		String password = getRanPassword(6);
		System.out.println("密码："+password);
		String sql = "update user set password='"+password+"' where phone='"+phone+"'";
		Db.update(sql);
		PhoneUtil.sendSms(password,phone,1);
		MapValue.map_login.remove(phone);
		render("user/userpassword.html");
	}
	
	public void checkEmail()
	{
		String userid = getSession().getAttribute("userid").toString();
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
				String code = getRanCodeEmail(6);
				System.out.println("emailcode:"+code);
				EmailUtil.sendEmail(code,email);
				String phone = getSession().getAttribute("phone").toString();
				UserEmailForm f = new UserEmailForm();
				f.setCode(code);
				f.setDate(new Date());
				MapValue.map_email.put(phone,f);
				renderText("no");
			}
			else
			{
				renderText("yes");
			}
		}
	}
	
	public void checkEmailForget()
	{
		String email = getPara("email");
		String sql = "select phone from user where email='"+email+"'";
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			String phone = record.getStr("phone");
			String code = getRanCodeEmail(6);
			System.out.println("emailcode:"+code);
			EmailUtil.sendEmail(code,email);
			UserEmailForm f = new UserEmailForm();
			f.setCode(code);
			f.setDate(new Date());
			MapValue.map_email.put(phone,f);
			renderText("yes");
		}
		else
		{
			renderText("no");
		}
	}
	
	public void checkSmsForget()
	{
		String phone = getPara("phone");
		String sql = "select id from user where phone='"+phone+"'";
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			String code = getRanCodePhone(4);
			PhoneUtil.sendSms(code,phone,0);
			System.out.println("短信验证码："+code);
			
			UserPhoneForm f = new UserPhoneForm();
			f.setCode(code);
			f.setDate(new Date());
			MapValue.map_phone.put(phone,f);
			
			renderText("yes");
		}
		else
		{
			renderText("no");
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
	
	public void listVoice()
	{
		List<VoiceForm> listVoice = new ArrayList<VoiceForm>();
		String userid = getSession().getAttribute("userid").toString();
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
		String userid = getSession().getAttribute("userid").toString();
		String companyname = getPara("companyname");
		String contact = getPara("contact");
		String contactphone = getPara("contactphone");
		String publicaccount = getPara("publicaccount");
		String licence = getPara("licence");
		String idcard = getPara("idcard");
		String safety = getPara("safety");
		String test = "select id from user_info where userid="+userid;
		Record record = Db.findFirst(test);
		if(record==null)
		{
			String sql = "insert into user_info (userid,companyname,contact,contactphone,publicaccount,licence,idcard,safety,ispass,create_time,modify_time) values ("+userid+",'"+companyname+"','"+contact+"','"+contactphone+"','"+publicaccount+"','"+licence+"','"+idcard+"','"+safety+"',0,now(),now())";
			Db.update(sql);
		}
		else
		{
			String sql = "update user_info set companyname='"+companyname+"',contact='"+contact+"',contactphone='"+contactphone+"',publicaccount='"+publicaccount+"',licence='"+licence+"',idcard='"+idcard+"',safety='"+safety+"',ispass=0,modify_time=now()";
			Db.update(sql);
		}
		render("user/userinfoupdateok.html");
	}
	
	public void voiceInfoEnter()
	{
		String userid = getSession().getAttribute("userid").toString();
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
		String phone = getSession().getAttribute("phone").toString();
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
		String phone = getSession().getAttribute("phone").toString();
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
		String publicaccount = "";
		String licence = "";
		String idcard = "";
		String safety = "";
		String ispass = "";
		String ispass_text = "";
		
		String userid = getSession().getAttribute("userid").toString();
		String phone = getSession().getAttribute("phone").toString();
		String sql = "select companyname,contact,contactphone,publicaccount,licence,idcard,safety,ispass from user_info where userid="+userid;
		Record record = Db.findFirst(sql);
		if(record!=null)
		{
			companyname = record.getStr("companyname");
			contact = record.getStr("contact");
			contactphone = record.getStr("contactphone");
			publicaccount = record.getStr("publicaccount");
			licence = record.getStr("licence");
			idcard = record.getStr("idcard");
			safety = record.getStr("safety");
			ispass = record.getStr("ispass");
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
		setAttr("publicaccount",publicaccount);
		setAttr("licence",licence);
		setAttr("idcard",idcard);
		setAttr("safety",safety);
		setAttr("ispass",ispass);
		setAttr("ispass_text",ispass_text);
		
		setAttr("phone",phone);
		
		render("user/userinfo.html");
	}
	
	public void userEditPassword()
	{
		String phone = getSession().getAttribute("phone").toString();
		String password = getPara("new_password");
		String sql = "update user set password='"+password+"' where phone='"+phone+"'";
		Db.update(sql);
		renderNull();
	}
	
	public void checkSrcPassword()
	{
		String phone = getSession().getAttribute("phone").toString();
		String src_password = getPara("src_password");
		String sql = "select id from user where phone='"+phone+"' and password='"+src_password+"'";
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
		String phone = getSession().getAttribute("phone").toString();
		setAttr("phone",phone);
		render("user/usereditpassword.html");
	}
	
	//登录出错次数过多，发送重置密码短信
	public void smsLogin()
	{
		String phone = getPara("loginphone");
		System.out.println("smsLoginPhone:"+phone);
		setAttr("phone",phone);
		
		String code = getRanCodePhone(4);
		PhoneUtil.sendSms(code,phone,0);
		
		System.out.println("短信验证码："+code);
		
		UserPhoneForm f = new UserPhoneForm();
		f.setCode(code);
		f.setDate(new Date());
		MapValue.map_phone.put(phone,f);
		
		render("user/usersmsauth.html");
	}
	
	public void checkLogin()
	{
		try
		{
			String phone = getPara("phone");
			String password = getPara("password");
			UserLoginForm login = MapValue.map_login.get(phone);
			if(login!=null)
			{
				int count = login.getCount();
				if(count>3)
				{
					renderText("more");
					return;
				}
			}
			String sql = "select id from user where phone='"+phone+"' and password='"+password+"'";
			Record record = Db.findFirst(sql);
			if(record==null)
			{
				String s = "select id from user where phone='"+phone+"'";
				Record r = Db.findFirst(s);
				if(r!=null)
				{
					if(MapValue.map_login.get(phone)==null)
					{
						UserLoginForm u = new UserLoginForm();
						u.setCount(1);
						u.setDate(new Date());
						MapValue.map_login.put(phone,u);
						renderText("nocount:3");
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
							renderText("nocount:"+(4-count));
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
				MapValue.map_login.remove(phone);
				renderText("ok");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	public void checkLogin()
	{
		try
		{
			String phone = getPara("phone");
			String password = getPara("password");
			String sql = "select trycount from user where phone='"+phone+"'";
			Record record = Db.findFirst(sql);
			if(record!=null)
			{
				int count = record.getInt("trycount");
				if(count>3)
				{
					renderText("more");
					return;
				}
			}
			sql = "select id from user where phone='"+phone+"' and password='"+password+"'";
			record = Db.findFirst(sql);
			if(record==null)
			{
				String s = "select trycount from user where phone='"+phone+"'";
				Record r = Db.findFirst(s);
				if(r!=null)
				{
					int count = r.getInt("trycount");
					count++;
					String s2 = "update user set trycount="+count+" where phone='"+phone+"'";
					Db.update(s2);
					if(count>3)
					{
						renderText("more");
						return;
					}
				}
				renderText("no");
			}
			else
			{
				String s = "update user set trycount=0 where phone='"+phone+"'";
				Db.update(s);
				renderText("ok");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	*/
	
	public void useraddok()
	{
		try
		{
			String phone = getPara("h_phone");
			String password = getPara("password");
			//String sql = "insert into user (phone,password,trycount) values ('"+phone+"','"+password+"',0)";
			String sql = "insert into user (phone,password,create_time) values ('"+phone+"','"+password+"',now())";
			Db.update(sql);
			getSession().setAttribute("phone",phone);
			
			sql = "select id from user where phone='"+phone+"'";
			Record record = Db.findFirst(sql);
			String userid = record.getStr("id");
			getSession().setAttribute("userid",userid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		render("user/userindex.html");
	}
	
	//用户注册短信验证
	public void sendsms()
	{
		String phone = getPara("phone");
		System.out.println("发送短信的手机号："+phone);
		try
		{
			String code = getRanCodePhone(4);
			PhoneUtil.sendSms(code,phone,0);
			
			System.out.println("短信验证码："+code);
			
			UserPhoneForm f = new UserPhoneForm();
			f.setCode(code);
			f.setDate(new Date());
			MapValue.map_phone.put(phone,f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	
	public void checkCode()
	{
		try
		{
			String phone = getPara("phone");
			String code = getPara("code");
			UserPhoneForm f = MapValue.map_phone.get(phone);
			if(f==null)
			{
				renderText("no");
			}
			else
			{
				String c = f.getCode();
				if(code.equals(c))
				{
					renderText("ok");
				}
				else
				{
					renderText("no");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
			String sql = "update user set password='"+password+"' where phone='"+phone+"'";
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
		String phone = getPara("phone");
		System.out.println("检测的手机号："+phone);
		String sql = "select id from user where phone='"+phone+"'";
		Record record = Db.findFirst(sql);
		if(record==null)
		{
			renderText("ok");
		}
		else
		{
			renderText("no");
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
