package com.dbkj.account.config;

import com.jfinal.plugin.IPlugin;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by DELL on 2017/09/21.
 */
public class SqlScanPlugin extends SqlContext implements IPlugin {

    private static final Logger logger= LoggerFactory.getLogger(SqlScanPlugin.class);

    private String directory;

    public SqlScanPlugin(String directory){
        this.directory=directory;
    }

    @Override
    public boolean start() {
        directory = Thread.currentThread().getContextClassLoader().getResource(directory).getPath();
        try {
            directory= URLDecoder.decode(directory,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        File dir=new File(directory);
        File[] files = dir.listFiles();
        for(int i=0;i<files.length;i++){
            //获取所有后缀为.sql.xml的文件
            if(files[i].isFile()&&files[i].getName().endsWith(".sql.xml")){
                try {
                    loadSql(files[i]);
                } catch (DocumentException e) {
                    if(logger.isErrorEnabled()){
                        logger.error(e.getMessage(),e);
                    }
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String directory = Thread.currentThread().getContextClassLoader().getResource("sql").getPath();
        directory= URLDecoder.decode(directory,"UTF-8");
        System.out.println(directory);
        File file=new File(directory);
//        System.out.println(new File(directory).exists());
        System.out.println(file.listFiles().length);
    }
}
