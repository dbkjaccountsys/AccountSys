package com.dbkj.account.handler;

import com.dbkj.account.config.sql.FreeMarkerSqlRender;
import com.dbkj.account.config.sql.ISqlRender;
import com.dbkj.account.dic.Constant;
import com.dbkj.account.model.Admin;
import com.dbkj.account.service.AuthorityService;
import com.dbkj.account.util.WebUtil;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 2017/09/22.
 */
public class MyDruidStatViewHandler extends DruidStatViewHandler{

    private AuthorityService authorityService=new AuthorityService();
    private static final Logger logger= LoggerFactory.getLogger(MyDruidStatViewHandler.class);

    private String visit;

    public MyDruidStatViewHandler(String visitPath) {
        super(visitPath);
        this.visit=visitPath;
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.startsWith(visit)) {
            isHandled[0]=true;
            //判断是否登录
            Object obj = request.getSession().getAttribute(Constant.CURRENT_USER);
            if(obj!=null){
                if(obj instanceof Admin){
                    Admin admin= (Admin) obj;
                    boolean result = authorityService.isPermissionOfAdmin(admin.getRoleId(),"/manage/druid");
                    if(result){
                        super.handle(target, request, response, isHandled);
                    }else{
                        noAuth(response,request);
                    }
                }else{
                    noAuth(response,request);
                }
            }else{//未登录跳转到登录界面
                noLogin(response);
            }
        }else{
            next.handle(target,request,response,isHandled);
        }


    }

    private void noAuth(HttpServletResponse response,HttpServletRequest request){
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        BufferedReader reader=null;
        try {
            PrintWriter writer=response.getWriter();
            reader=new BufferedReader(new FileReader(WebUtil.getRootPath(request)+"/pages/other/401.html"));
            ISqlRender render=new FreeMarkerSqlRender();
            String line=null;
            String contextPath=request.getContextPath();
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("ctx",contextPath);
            while((line=reader.readLine())!=null){
                line=render.render(line,map);
                writer.println(line);
            }
            writer.flush();
        } catch (IOException e) {
            if(logger.isErrorEnabled()){
                logger.error(e.getMessage(),e);
            }
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    if(logger.isErrorEnabled()){
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        }
    }

    private void noLogin(HttpServletResponse response){
        try {
            response.sendRedirect("/manage/login");
        } catch (IOException e) {
            if(logger.isErrorEnabled()){
                logger.error(e.getMessage(),e);
            }
        }
    }
}
