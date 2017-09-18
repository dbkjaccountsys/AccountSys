package com.dbkj.account.sys;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.dbkj.account.interceptor.AdminAuthorityTemplateDirectiveInterceptor;
import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.interceptor.LoginInterceptor;
import com.dbkj.account.interceptor.ResetPasswordInterceptor;
import com.dbkj.account.interceptor.UserAuthorityTemplateDirectiveInterceptor;
import com.dbkj.account.sys.form.FavorableForm;
import com.dbkj.account.sys.form.MoneyRecordForm;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

//import pac.constants.Constants;

@Clear({LoginInterceptor.class,AuthInterceptor.class,AdminAuthorityTemplateDirectiveInterceptor.class,UserAuthorityTemplateDirectiveInterceptor.class,ResetPasswordInterceptor.class})
public class MoneyController extends Controller
{
	public void index()
	{
		String sql = "select id,name,date_format(begintime,'%Y-%m-%d %H:%i:%s') as begintime,date_format(endtime,'%Y-%m-%d %H:%i:%s') as endtime,status,rate,mincharge,maxcharge from favorable where now()>=begintime and now()<=endtime and status=0";
		List<Record> list = Db.find(sql);
		List<FavorableForm> list2 = new ArrayList<FavorableForm>();
		for(int i=0;i<list.size();i++)
		{
			Record record = list.get(i);
			FavorableForm f = new FavorableForm();
			f.setId(record.getStr("id"));
			f.setName(record.getStr("name"));
			f.setBegintime(record.getStr("begintime"));
			f.setEndtime(record.getStr("endtime"));
			f.setStatus(record.getStr("status"));
			f.setRate(record.getStr("rate"));
			f.setMincharge(record.getStr("mincharge"));
			f.setMaxcharge(record.getStr("maxcharge"));
			list2.add(f);
		}
		setAttr("favorable",list2);
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		sql = "select charge from user where id="+userid;
		Record record = Db.findFirst(sql);
		String charge = record.getStr("charge");
		setAttr("charge",charge);
		render("money/addmoney.html");
	}
	
	public void addmoneystart()
	{
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String money = getPara("money");
		String favorable_money = getPara("favorable_money");
		int pos = money.indexOf(".");
		if(pos==-1)
		{
			money += ".00";
		}
		else
		{
			String s = money.substring(pos+1);
			if(s.length()==1)
			{
				money += "0";
			}
		}
		pos = favorable_money.indexOf(".");
		if(pos==-1)
		{
			favorable_money += ".00";
		}
		else
		{
			String s = favorable_money.substring(pos+1);
			if(s.length()==1)
			{
				favorable_money += "0";
			}
		}
		String type = getPara("type");
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String orderno = sf.format(new Date()) + getRanId(8);
		System.out.println("充值金额："+money);
		System.out.println("充值类型："+type);
		System.out.println("订单号："+orderno);
		setAttr("money",money);
		setAttr("favorable_money",favorable_money);
		setAttr("type",type);
		setAttr("orderno",orderno);
		setAttr("userid",userid);
		render("money/addmoneystart.html");
	}
	
	
	public void pay()
	{
		try
		{
			String userid = getPara("userid");
			AlipayClient alipayClient = new DefaultAlipayClient(Config.gatewayUrl,Config.appid,Config.privatekey,"json","UTF-8",Config.publickey,Config.signtype);
			AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
			alipayRequest.setReturnUrl(Config.returnUrl);
			alipayRequest.setNotifyUrl(Config.notifyUrl);
			System.out.println(Config.notifyUrl);
			System.out.println(alipayRequest.getNotifyUrl());
			
			String out_trade_no = getPara("tradeno");
			String amount = getPara("amount");
			String favorable_amount = getPara("favorable_amount");
			float f = Float.parseFloat(amount) + Float.parseFloat(favorable_amount);
			String total_amount = f + "";
			String subject = "大坝充值付款";
			//String body = "商品描述";
			
			alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
					+ "\"total_amount\":\""+ amount +"\","
					+ "\"subject\":\""+ subject +"\","
					//+ "\"body\":\""+ body +"\","
					+ "\"timeout_express\":\""+ Config.timeoutExpress +"m\","
					+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
			
			String sql = "insert into user_recharge (userid,time,charge,realcharge,chargetype,serialnum) values ("+userid+",now(),"+amount+","+total_amount+",0,'"+out_trade_no+"')";
			Db.update(sql);
			String result = alipayClient.pageExecute(alipayRequest).getBody();
			//System.out.println("pay_result:"+result);
			renderHtml(result);
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	
	public void payNotify()
	{
		System.out.println("xccxxxxxxxxxxxxxxxxxxxxxxx");
		String out_trade_no = getRequest().getParameter("out_trade_no");
		String sql = "select realcharge,userid from user_recharge where serialnum='"+out_trade_no+"'";
		Record record = Db.findFirst(sql);
		String total_amount = record.getStr("realcharge");
		String userid = record.getStr("userid");
		System.out.println("支付返回："+sql);
		sql = "update user set charge=charge+"+total_amount + " where id="+userid;
		System.out.println("sql:"+sql);
		Db.update(sql);
		System.out.println("支付返回："+sql);
		sql = "update user_recharge set status=2 where serialnum='"+out_trade_no+"'";
		Db.update(sql);
		System.out.println("支付返回："+sql);
		renderText("success");
	}
	
	/*
	public void pay()
	{
		String tradeno = getPara("tradeno");
		String amount = getPara("amount");
		setAttr("tradeno",tradeno);
		setAttr("amount",amount);
		setAttr("subject","大坝科技收款");
		renderJsp("money/alipay_pay.jsp");
	}
	*/
	
	public void payReturn()
	{
		//String userid = getSession().getAttribute(Config.sessionUserid).toString();
		//商家交易号
		String out_trade_no = getPara("out_trade_no");
		//支付宝交易号
		//String trade_no = getPara("trade_no");
		//String total_amount = getPara("total_amount");
		
		//System.out.println("out_trade_no:"+out_trade_no);
		//System.out.println("trade_no:"+trade_no);
		//System.out.println("total_amount:"+total_amount);
		
		String sql = "select realcharge from user_recharge where serialnum='"+out_trade_no+"'";
		Record record = Db.findFirst(sql);
		String total_amount = record.getStr("realcharge");
		setAttr("total_amount",total_amount);
		setAttr("count",Config.modifyEmailPhoneCount);
		setAttr("flag","0");
		render("new/index.html");
	}
	
	public void payReturnOk()
	{
		String total_amount = getPara("total_amount");
		setAttr("total_amount",total_amount);
		render("money/addmoneyok.html");
	}
	
	//测试生成二维码
	/*
	public void code()
	{
		try
		{
			String url = "http://www.baidu.com";
			int width = 300;
			int height = 300;
			BitMatrix bitMatrix = new MultiFormatWriter().encode(url,BarcodeFormat.QR_CODE,width,height);
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			image.createGraphics();
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0,0,width,height);
			graphics.setColor(Color.BLACK);
			for(int i=0;i<width;i++)
			{
				for(int j=0;j<height;j++)
				{
					if(bitMatrix.get(i,j))
					{
						graphics.fillRect(i,j,1,1);
					}
				}
			}
			ImageIO.write(image,"png",getResponse().getOutputStream());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		renderNull();
	}
	*/
	
	public void moneyRecordList()
	{
		String startdate = getPara("startdate");
		String enddate = getPara("enddate");
		System.out.println("startdate:"+startdate);
		System.out.println("enddate:"+enddate);
		
		if(startdate==null)
		{
			Date date1 = new Date();
			Calendar ca = Calendar.getInstance();
			ca.setTime(date1);
			ca.add(Calendar.MONTH,-1);
			Date date2 = ca.getTime();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			startdate = sf.format(date2);
			enddate = sf.format(date1);
		}
		
		int no = 0;
		//int pagecount = Constants.pagecount;
		int pagecount = Integer.parseInt(Config.pageCount);
		int curpage = 1;
		int total = 0;
		int totalpage = 0;
		if(getPara("curpage")!=null)
		{
			curpage = Integer.parseInt(getPara("curpage"));
		}
		no = (curpage-1) * pagecount + 1;
		
		String userid = getSession().getAttribute(Config.sessionUserid).toString();
		String sql = "select id,userid,date_format(time,'%Y-%m-%d %H:%i:%s') as time,charge,realcharge,chargetype,serialnum,status from user_recharge where userid="+userid;
		sql += " and date_format(time,'%Y-%m-%d')>='"+startdate+"' and date_format(time,'%Y-%m-%d')<='"+enddate+"' order by time desc limit "+(curpage-1)*pagecount+","+pagecount;
		System.out.println("sql:"+sql);
		
		List<Record> list = Db.find(sql);
		List<MoneyRecordForm> list2 = new ArrayList<MoneyRecordForm>();
		for(int i=0;i<list.size();i++)
		{
			Record record = list.get(i);
			MoneyRecordForm f = new MoneyRecordForm();
			f.setId(record.getStr("id"));
			f.setUserid(record.getStr("userid"));
			f.setTime(record.getStr("time"));
			f.setCharge(record.getStr("charge"));
			f.setRealcharge(record.getStr("realcharge"));
			String chargetype = record.getStr("chargetype");
			String chargetypestr = "";
			if(chargetype.equals("0"))
			{
				chargetypestr = "支付宝";
			}
			else if(chargetype.equals("1"))
			{
				chargetypestr = "微信";
			}
			else if(chargetype.equals("2"))
			{
				chargetypestr = "赠送";
			}
			f.setChargetype(chargetype);
			f.setChargetypestr(chargetypestr);
			f.setSerialnum(record.getStr("serialnum"));
			String status = record.getStr("status");
			String statusstr = "";
			if(status.equals("0"))
			{
				statusstr = "未支付";
			}
			else if(status.equals("1"))
			{
				statusstr = "支付超时";
			}
			else if(status.equals("2"))
			{
				statusstr = "已支付";
			}
			f.setStatus(status);
			f.setStatusstr(statusstr);
			list2.add(f);
		}
		
		String sqltotal = "select count(id) as c from user_recharge where userid="+userid;
		sqltotal += " and date_format(time,'%Y-%m-%d')>='"+startdate+"' and date_format(time,'%Y-%m-%d')<='"+enddate+"'";
		Record r = Db.findFirst(sqltotal);
		long l_total = r.getLong("c");
		total = (int)l_total;
		if(total%pagecount!=0)
		{
			totalpage = total/pagecount+1;
		}
		else
		{
			totalpage = total/pagecount;
		}
		
		setAttr("list",list2);
		setAttr("startdate",startdate);
		setAttr("enddate",enddate);
		
		setAttr("totalpage",totalpage);
		setAttr("curpage",curpage);
		setAttr("no",no);
		setAttr("total", total);
		setAttr("pagecount",pagecount);
		setAttr("url",getRequest().getContextPath()+"/money/moneyRecordList");
		
		render("money/moneyrecord.html");
	}
	
	public String getRanId(int len)
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