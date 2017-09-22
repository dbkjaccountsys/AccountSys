package com.dbkj.account.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * log4j异步输出处理类
 * Created by DELL on 2017/09/21.
 */
public class Log4jAsyncWriter extends Writer {

    private static final Logger logger= LoggerFactory.getLogger(Log4jAsyncWriter.class);

    /**
     * AsyncContext队列
     */
    @SuppressWarnings("Since15")
    private static final Queue<AsyncContext> asyncQueue=new ConcurrentLinkedDeque<AsyncContext>();

    /**
     * log消息队列
     */
    @SuppressWarnings("Since15")
    private static final BlockingQueue<String> msgQueue=new LinkedBlockingDeque<String>();

    /**
     * 添加到AsyncContext对象到队列
     * @param asyncContext
     */
    public static void addAsyncContext(AsyncContext asyncContext){
        asyncQueue.add(asyncContext);
    }

    /**
     * 删除AsyncContext队列对象
     * @param asyncContext
     */
    public static void deleteAsyncContext(AsyncContext asyncContext){
        asyncQueue.remove(asyncContext);
    }

    /**
     * 构造AsyncContextQueueWriter异步线程，当消息队列中被放入数据，将释放take方法的阻塞，将数据发送到http response流上
     */
    public Log4jAsyncWriter(){
        Thread notifierThread=new Thread(new Runnable() {
            public void run() {
                while(true){
                    try{
                        String message=msgQueue.take();
                        for(AsyncContext ac:asyncQueue){
                            PrintWriter printWriter=ac.getResponse().getWriter();
                            printWriter.println("<script type=\"text/javascript\">\nwindow.parent.update(\""+
                                    message.replaceAll("\n","").replaceAll("\r","")+"\");</script>\n");
                        }
                    }catch (InterruptedException e){
                        if(logger.isErrorEnabled()){
                            logger.error("Log4jAsyncWriter InterruptedException 异常！msgQueue.take()",e);
                        }
                    } catch (IOException e) {
                        if(logger.isErrorEnabled()){
                            logger.error(e.getMessage(),e);
                        }
                    }
                }
            }
        });
        notifierThread.setName("little-ant-Thread-Log4jAsyncWriter");
        notifierThread.start();
    }

    public void write(char[] cbuf, int off, int len) throws IOException {

    }

    public void flush() throws IOException {

    }

    public void close() throws IOException {

    }
}
