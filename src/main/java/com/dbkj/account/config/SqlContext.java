package com.dbkj.account.config;

import com.dbkj.account.config.sql.FreeMarkerSqlRender;
import com.dbkj.account.config.sql.ISqlRender;
import com.jfinal.kit.StrKit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by DELL on 2017/09/21.
 */
public class SqlContext {

    private static final Logger logger= LoggerFactory.getLogger(SqlContext.class);
    private static final Map<String,String> sqlMap=new HashMap<>();

    private static final List<ISqlRender> sqlRenderList= new ArrayList<>();

    static {
        sqlRenderList.add(new FreeMarkerSqlRender());
    }

    protected void putSql(String key,String value){
        sqlMap.put(key,value);
    }

    /**
     * 根据sql id获取sql
     * @param key
     * @param param
     * @param renderType 解析类型 @see {@link RenderType}
     * @return
     */
    public static String getSql(String key,Map<String,Object> param,RenderType renderType){
        String sql = sqlMap.get(key);
        if(StrKit.isBlank(sql)){
            if(logger.isErrorEnabled()){
                logger.error("sql语句不存在，sqlId是：{}",key);
            }
            return null;
        }
        if(param==null){
            param=new HashMap<>();
        }

        for(ISqlRender sqlRender:sqlRenderList){
            if(sqlRender.isMatch(renderType)){
                try {
                    sql = sqlRender.render(sql,param);
                } catch (Exception e) {
                    if(logger.isErrorEnabled()){
                        logger.error(e.getMessage(),e);
                    }
                    throw new RuntimeException(e);
                }
            }
        }
        return sql.replaceAll("[\\s]{2,}"," ");
    }

    public static String getSqlByFreeMarker(Class<?> clazz,String id,Map<String,Object> param){
        String key=clazz.getName()+"."+id;
        return getSql(key,param,RenderType.FreeMarker);
    }

    public static String getSqlByFreeMarker(Class<?> clazz,String id){
        return getSqlByFreeMarker(clazz,id,null);
    }

    protected void loadSql(File file) throws DocumentException {
        SAXReader reader=new SAXReader();
        Document document=reader.read(file);
        Element root = document.getRootElement();
        String namespace = root.attributeValue("namespace");

        //获取所有的子节点
        List<Element> nodeList=root.elements();
        for(Element element:nodeList){
            if("sql".equals(element.getName())){
                String key=namespace+"."+element.attributeValue("id");
                String sql=element.getTextTrim();
                sqlMap.put(key,sql);
            }
        }
    }

    public static enum RenderType{
        FreeMarker, Velocity, Beetl;
    }
}
