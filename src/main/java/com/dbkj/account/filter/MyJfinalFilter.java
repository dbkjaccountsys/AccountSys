package com.dbkj.account.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.JFinalFilter;

/**
 * 自定义JfinalFilter
 * @author DELL
 *
 */
public class MyJfinalFilter extends JFinalFilter{
	
	//不过滤的路径参数名称
	public static final String PARAM_NAME_EXCLUSIONS = "exclusions";
	//不过滤的路径
	private Set<String> excludesPattern;
	private String contextPath;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		String exclusions = filterConfig.getInitParameter(PARAM_NAME_EXCLUSIONS);
        if (exclusions != null && exclusions.trim().length() != 0) {
            excludesPattern = new HashSet<String>(Arrays.asList(exclusions.split("\\s*,\\s*")));
        }
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest)req;
		contextPath=request.getContextPath();
		if(isExclusion(request.getRequestURI())){
			chain.doFilter(req, res);
		}else{
			super.doFilter(req, res, chain);
		}
		
	}
	
	/**
	 * 判断路径是否应该被排除
	 * @param requestURI
	 * @return
	 */
	private boolean isExclusion(String requestURI){
		if(excludesPattern==null||requestURI==null){
			return false;
		}
		if(contextPath!=null&&requestURI.startsWith(contextPath)){
			requestURI=requestURI.substring(contextPath.length());
			if(!requestURI.startsWith("/")){
				requestURI="/"+requestURI;
			}
		}
		
		for(String pattern:excludesPattern){
			if(matches(pattern, requestURI)){
				return true;
			}
		}
		return false;
	}
	
	private boolean matches(String pattern,String source){
		if(pattern==null||source==null){
			return false;
		}
		pattern=pattern.trim();
		source=source.trim();
		if(pattern.endsWith("*")){
			//pattern:/druid/*
			int length=pattern.length()-1;
			if(source.length()>length){
				if(pattern.substring(0,length).equals(source.substring(0, length))){
					return true;
				}
			}
		}else if(pattern.startsWith("*")){
			//pattern:*.js
			int length=pattern.length()-1;
			if(source.length()>length&&source.endsWith(pattern.substring(1))){
				return true;
			}
		}else if(pattern.contains("*")){
			//pattern: /druid/*/index.html
			int start=pattern.indexOf("*");
			int end=pattern.lastIndexOf("*");
			if(source.startsWith(pattern.substring(0,start))&&
					source.endsWith(pattern.substring(end+1))){
				return true;
			}
		}else{
			if(source.equals(pattern)){
				return true;
			}
		}
		return false;
	}

}
