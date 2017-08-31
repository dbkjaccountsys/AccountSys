package com.dbkj.account.util;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.kit.StrKit;

public class WebUtil {

	/**
	 * 获取客户端IP
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request){
		String ip=request.getHeader("X-Forwarded-For");
		if(!StrKit.isBlank(ip)&&!"unKnown".equalsIgnoreCase(ip)){
			//多次反向代理后会出现多个IP，第一个IP才是真实的IP
			int index=ip.indexOf(",");
			if(index!=-1){
				return ip.substring(0, index);
			}else{
				return ip;
			}
		}
		ip=request.getHeader("X-Real-IP");
		if(!StrKit.isBlank(ip)&&"unKnown".equalsIgnoreCase(ip)){
			return ip;
		}
		return request.getRemoteAddr();
	}
	
	/**
	 * 判断是为ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request){
		return (request.getHeader("X-Requested-With") != null&&
				"XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With").toString()));
	}
	
	/**
	 * 获取当前请求action
	 * @param request
	 * @return
	 */
	public static String getAction(HttpServletRequest request){
		String requestURI=request.getRequestURI();
		String contextPath=request.getContextPath();
		if(contextPath!=null&&requestURI.startsWith(contextPath)){
			requestURI=requestURI.substring(contextPath.length());
			if(!requestURI.startsWith("/")){
				requestURI="/"+requestURI;
			}
		}
		return requestURI;
	}
}
