package com.dbkj.account.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.model.Action;

/**
 * Created by DELL on 2017/08/14.
 */
public class SqlUtil {

    private static final Logger logger= LoggerFactory.getLogger(SqlUtil.class);

    private static long lastModified=-1L;

    private static Map<String,String> SQL_MAP=new ConcurrentHashMap<String, String>();

    private static String filePath=null;//存储文件路径

    private static Object lock=new Object();

    @SuppressWarnings("unchecked")
	private static void load(String fileName){
        if(filePath==null){
            synchronized (lock){
                if(filePath==null){
                    filePath = Thread.currentThread().getContextClassLoader().getResource(fileName).getPath();
                    try {
                        filePath= URLDecoder.decode(filePath,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        }
        File file=new File(filePath);
        long currentTime=file.lastModified();
        //判断文件是否有修改，修改过就重新加载
        if(currentTime>lastModified){
            if(logger.isInfoEnabled()){
                logger.info("重新加载Sql文件！");
            }
            SAXReader reader=new SAXReader();
            try {
                Document document = reader.read(file);
                //获取根节点
                Element root=document.getRootElement();
                //获取根节点下的所有子节点
				List<Element> list = root.elements();
                for(Element element:list){
                    String className=element.attributeValue("class");
                    //获取group节点下的子节点
                    List<Element> list1=element.elements();
                    for(Element element1:list1){
                        String name=className+"."+element1.attributeValue("id");
                        String sql=element1.getTextTrim();
//                        sql=sql.replaceAll(" {2,}", " ");//替换字符串中的多个空格为一个空格
                        SQL_MAP.put(name,sql);
                    }
                }
            } catch (DocumentException e) {
                logger.error(e.getMessage(),e);
            }
            lastModified=currentTime;//修改文件被修改的时间
        }
    }

    public static String getSql(Class<?> clazz,String sqlId){
        load("sql.xml");
        return SQL_MAP.get(clazz.getName()+"."+sqlId);
    }

    public static void main(String[] args) {
        String sql=getSql(Action.class,"getActionsUrlByRoleId");
        System.out.println(sql);
    }
}
