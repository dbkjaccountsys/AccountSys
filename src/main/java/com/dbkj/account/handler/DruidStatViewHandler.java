package com.dbkj.account.handler;

import com.alibaba.druid.support.http.StatViewServlet;
import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.druid.IDruidStatViewAuth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by DELL on 2017/09/21.
 */
public class DruidStatViewHandler extends Handler {

    private IDruidStatViewAuth auth;
    private String visitPath="/druid";
    private StatViewServlet servlet=new JFinalStatViewServlet();

    public DruidStatViewHandler(String visitPath){
        this(visitPath, new IDruidStatViewAuth() {
            public boolean isPermitted(HttpServletRequest request) {
                return true;
            }
        });
    }

    public DruidStatViewHandler(String visitPath,IDruidStatViewAuth auth){
        if(StrKit.isBlank(visitPath)){
            throw new IllegalArgumentException("visitPath cannot be blank.");
        }
        if(auth==null){
            throw new IllegalArgumentException("druidViewAuth cannot be null.");
        }
        visitPath=visitPath.trim();
        if(!visitPath.startsWith("/")){
            visitPath="/"+visitPath;
        }
        this.visitPath=visitPath;
        this.auth=auth;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if(target.startsWith(visitPath)){
            isHandled[0]=true;

            if(target.equals(visitPath)&&!target.equals("/index.html")){
                HandlerKit.redirect(target+="/index.html",request,response,isHandled);
            }

            try {
                servlet.service(request,response);
            } catch (ServletException|IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            next.handle(target,request,response,isHandled);
        }
    }

    class JFinalStatViewServlet extends StatViewServlet {

        private static final long serialVersionUID = 4836523505141746719L;

        public boolean isPermittedRequest(HttpServletRequest request){
            return auth.isPermitted(request);
        }

        @Override
        public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//            super.service(request, response);
            String contextPath=request.getContextPath();
            String requestURI=request.getRequestURI();

            response.setCharacterEncoding("utf-8");

            if(contextPath==null){
                contextPath="";
            }
            int index=contextPath.length()+visitPath.length();
            String uri=requestURI.substring(0,index);
            String path=requestURI.substring(index);

            if(!isPermittedRequest(request)){
                path="/nopermit.html";
                returnResourceFile(path,uri,response);
                return;
            }

            if("/submitLogin".equals(path)){
                String usernameParam=request.getParameter(PARAM_NAME_USERNAME);
                String passwordParam=request.getParameter(PARAM_NAME_PASSWORD);
                if(username.equals(usernameParam)&&password.equals(passwordParam)){
                    request.getSession().setAttribute(SESSION_USER_KEY,username);
                    response.getWriter().print("success");
                }else{
                    response.getWriter().print("error");
                }
                return;
            }

            if(isRequireAuth()&&!ContainsUser(request)
                    &&!("login.html".equals(path)
                        ||path.startsWith("/css")
                        ||path.startsWith("/js")
                        ||path.startsWith("/img"))){
                if(contextPath==null||contextPath.equals("")||contextPath.equals("/")){
                    response.sendRedirect("/druid/login.html");
                }else{
                    if("".equals(path)){
                        response.sendRedirect("druid/login.html");
                    }else{
                        response.sendRedirect("login.html");
                    }
                }
                return;
            }

            if("".equals(path)){
                if(contextPath==null||contextPath.equals("")||contextPath.equals("/")){
                    response.sendRedirect("/druid/index.html");
                }else{
                    response.sendRedirect("druid/index.html");
                }
                return;
            }

            if("/".equals(path)){
                response.sendRedirect("index.html");
                return;
            }

            if(path.indexOf(".json")>=0){
                String fullUrl=path;
                if(request.getQueryString()!=null&&request.getQueryString().length()>0){
                    fullUrl+="?"+request.getQueryString();
                }
                response.getWriter().print(process(fullUrl));
                return;
            }

            //find file in resources path
            returnResourceFile(path,uri,response);
        }
    }
}
