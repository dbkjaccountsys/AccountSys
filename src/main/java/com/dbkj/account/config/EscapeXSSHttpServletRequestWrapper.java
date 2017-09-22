package com.dbkj.account.config;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * 重写get参数或属性系列方法，参数值增加html转义
 * Created by DELL on 2017/09/20.
 */
public class EscapeXSSHttpServletRequestWrapper extends HttpServletRequestWrapper {
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public EscapeXSSHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if(!StringUtils.isBlank(value)){
            value= StringEscapeUtils.escapeHtml4(value);
        }
        return value;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        HashMap<String,String[]> map = (HashMap<String, String[]>) new HashMap<String, String[]>(super.getParameterMap())
                .clone();

        for(Map.Entry<String,String[]> entry:map.entrySet()){
            String[] values=entry.getValue();
            for(int i=0;i<values.length;i++){
                values[i]=StringEscapeUtils.escapeHtml4(values[i]);
            }
            entry.setValue(values);
        }
        return map;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        for(int i=0;i<values.length;i++){
            values[i]=StringEscapeUtils.escapeHtml4(values[i]);
        }
        return values;
    }

    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if(value instanceof String){
            value=StringEscapeUtils.escapeHtml4(String.valueOf(value));
        }
        return value;
    }
}
