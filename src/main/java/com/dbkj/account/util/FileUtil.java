package com.dbkj.account.util;

import java.io.File;

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
}
