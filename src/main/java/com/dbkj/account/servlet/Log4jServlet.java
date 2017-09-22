package com.dbkj.account.servlet;

import com.jfinal.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by DELL on 2017/09/21.
 */
public class Log4jServlet extends HttpServlet {

    private static final long serialVersionUID = -4529444145612582420L;

    private static final Logger logger= LoggerFactory.getLogger(Log4jServlet.class);

    /**
     * 将客户端注册到监听Logger的消息队列
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.setHeader("Cache-Control","private");
        resp.setHeader("Pragma","no-cache");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer=resp.getWriter();
        //for IE
        writer.println("<!-- Comet is programming technique that enables web servers to send data to the client without " +
                "having any need for the client to request it. -->\n");
        writer.flush();

        final AsyncContext asyncContext=req.getAsyncContext();
        asyncContext.setTimeout(1*60*60*1000);//设置超时时间为1小时
        asyncContext.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent event) throws IOException {
                asyncContext.getResponse().getWriter().close();
                Log4jAsyncWriter.deleteAsyncContext(asyncContext);
                if(logger.isDebugEnabled())logger.debug("AsyncListener onComplete");
            }

            public void onTimeout(AsyncEvent event) throws IOException {
                asyncContext.getResponse().getWriter().close();
                Log4jAsyncWriter.deleteAsyncContext(asyncContext);
                if(logger.isDebugEnabled())logger.debug("AsyncListener onTimeout");
            }

            public void onError(AsyncEvent event) throws IOException {
                asyncContext.getResponse().getWriter().close();
                Log4jAsyncWriter.deleteAsyncContext(asyncContext);
                if(logger.isDebugEnabled())logger.debug("AsyncListener onError");
            }

            public void onStartAsync(AsyncEvent event) throws IOException {
                if(logger.isDebugEnabled()) logger.debug("AsycnListener onStartAsync");
            }
        });
        Log4jAsyncWriter.addAsyncContext(asyncContext);
    }
}
