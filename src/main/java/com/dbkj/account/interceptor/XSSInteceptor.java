package com.dbkj.account.interceptor;

import com.dbkj.account.config.EscapeXSSHttpServletRequestWrapper;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * XSS拦截器
 * 描述：重写get参数或属性系列方法，参数值增加html转义
 * Created by DELL on 2017/09/20.
 */
public class XSSInteceptor implements Interceptor {

    public void intercept(Invocation inv) {
        HttpServletRequest request=inv.getController().getRequest();

        if(!isMultipartRequest(request)){
            inv.getController().setHttpServletRequest(new EscapeXSSHttpServletRequestWrapper(request));
        }

        inv.invoke();
    }

    /**
     * 是否enctype="multipart/from-data"
     * @param request
     * @return
     */
    private boolean isMultipartRequest(HttpServletRequest request){
        String contentType=request.getContentType();
        return contentType!=null&&contentType.toLowerCase().indexOf("multipart")!=-1;
    }
}
