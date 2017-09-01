package com.dbkj.account.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

	/**
	 * 删除目录及目录下的文件
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir){
		if(!dir.exists()){
			return false;
		}
		if(dir.isDirectory()){
			String[] children=dir.list();
			for(int i=0;i<children.length;i++){
				boolean success=deleteDir(new File(dir,children[i]));
				if(!success){
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	/**
	 * 删除目录及目录下的文件
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(String dir){
		return deleteDir(new File(dir));
	}
	
	/**
	 * 复制文件
	 * @param fromFile 
	 * @param toFile
	 * @throws IOException 
	 */
	public static void copyFile(File fromFile,File toFile) throws IOException{
		if(toFile.exists()){
			throw new IOException("文件已存在");
		}else{
			createFile(toFile, true);
		}
		InputStream is=null;
		FileOutputStream fos=null;
		try{
			is=new FileInputStream(fromFile);
			fos=new FileOutputStream(toFile);
			byte[] buffer=new byte[2014];
			while(is.read(buffer)!=-1){
				fos.write(buffer);
			}
		}finally {
			if(is!=null){
				is.close();
			}
			if(fos!=null){
				fos.close();
			}
		}
		
	}
	
	/**
	 * 创建文件
	 * @param file
	 * @param isFile
	 * @throws IOException 
	 */
	public static void createFile(File file,boolean isFile) throws IOException{
		if(!file.exists()){
			File parentFile=file.getParentFile();
			if(!parentFile.exists()){//不存在父目录
				createFile(parentFile, false);
			}else{//存在父目录
				if(isFile){
					try {
						file.createNewFile();
					} catch (IOException e) {
						throw new IOException(e);
					}
				}else{
					file.mkdir();
				}
			}
		}
	}
}
