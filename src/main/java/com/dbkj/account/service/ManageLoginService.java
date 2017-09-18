package com.dbkj.account.service;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dic.UserType;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.UserLog;
import com.dbkj.account.util.DateUtil;
import com.dbkj.account.util.SqlUtil;
import com.dbkj.account.util.WebUtil;
import com.dbkj.account.vo.LoginVo;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ManageLoginService {
	
	private LogService logService=new LogService();
	
	/**
	 * 登陆验证
	 * @param loginVo
	 * @param request
	 */
	public boolean login(LoginVo loginVo,HttpServletRequest request){
		String username=loginVo.getUsername();
		String password=DigestUtils.md5Hex(loginVo.getPassword());
		HttpSession session=request.getSession();
		
		String operaContent="登陆操作";
		
		Admin admin=Admin.dao.findFirst(SqlUtil.getSql(Admin.class, "findByUsernameAndPassword"),username,password);
		if(admin!=null){
			session=reGenerateSessionId(request);
			session.setAttribute(Constant.CURRENT_USER, admin);
			//添加操作日志
			logService.addLog(request, operaContent, OperaResult.SUCCESS, null);
			return true;
		}
		//添加操作日志
		admin=Admin.dao.findFirst(SqlUtil.getSql(Admin.class, "getIdByUsername"),username);
		if(admin!=null){
			logService.addLog(request,UserType.ADMIN,admin.getId(), operaContent, OperaResult.FAIL, null);
		}
		request.setAttribute("user", loginVo);
		Res res=I18n.use("zh_CN");
		request.setAttribute("errorMsg",res.get("login.username.or.password.incorrect"));
		return false;
	}
	
	/** 
	 * 重置sessionid，原session中的数据自动转存到新session�? 
	 * @param request 
	 */  
	public HttpSession reGenerateSessionId(HttpServletRequest request){  
	      
	    HttpSession session = request.getSession();  
	      
	    //首先将原session中的数据转移至一临时map�?  
	    Map<String,Object> tempMap = new HashMap<String,Object>();  
	    Enumeration<String> sessionNames = session.getAttributeNames();  
	    while(sessionNames.hasMoreElements()){  
	        String sessionName = sessionNames.nextElement();  
	        tempMap.put(sessionName, session.getAttribute(sessionName));  
	    }  
	      
	    //注销原session，为的是重置sessionId  
	    session.invalidate();  
	      
	    //将临时map中的数据转移至新session  
	    session = request.getSession();  
	    for(Map.Entry<String, Object> entry : tempMap.entrySet()){  
	        session.setAttribute(entry.getKey(), entry.getValue());  
	    }  
	    return session;
	}  
	
	/**
	 * 判断用户是否登录错误次数超过限制
	 * @param username
	 * @param request
	 * @return
	 */
	public boolean isLoginTooManyTimes(String username,HttpServletRequest request){
		UserLog.dao.createTable();//先创建表，防止查询错�?
		String sql=SqlUtil.getSql(UserLog.class, "getLoginFailTimes")
				.replace(Constant.BASE_LOG_TABLE, Constant.BASE_LOG_TABLE+"_"+DateUtil.getDateStr("yyyyMM"));
		//登陆错误监测时间
		int loginFailDuration = PropKit.getInt("loginFailDuration",5);
		//登陆错误次数
		int loginFailTimes=PropKit.getInt("loginFailTimes",3);
		Date now=new Date();
		Integer operaType=OperaTypeService.getOperaType(WebUtil.getAction(request));
		
		Record record=Db.findFirst(sql,operaType,DateUtil.addMinutes(now,loginFailDuration*(-1)),
				now,OperaResult.FAIL.getValue(),loginFailTimes);
		return record.getInt("times")>=loginFailTimes;
	}
	
	/**
	 * 注销
	 * @param session
	 */
	public void logout(HttpSession session){
		session.invalidate();
	}
	
	
	public static void main(String[] args) {
		//c4ca4238a0b923820dcc509a6f75849b
		String encoded=DigestUtils.md5Hex("dbkjadmin");
		System.out.println(encoded);
	}
}
