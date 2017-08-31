package com.dbkj.account.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	/**
	 * 在给定的日期以天为单位计算日期
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date,int days){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH,days);
		return calendar.getTime();
	}

	/**
	 * 获取指定格式的当前时间字符串
	 * @param pattern 日期格式字符串
	 * @return
	 */
	public static String getDateStr(String pattern){
		return getDateStr(new Date(),pattern);
	}
	
	public static String getDateStr(Date date,String pattern){
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	public static Date addMinutes(Date date,int minutes){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE,minutes);
		return calendar.getTime();
	}
	
	public static void main(String[] args) {
		Date now=new Date();
		String pattern="yyyy-MM-dd HH:mm:ss";
		System.out.println("当前时间："+getDateStr(now,pattern));
		Date date=addMinutes(now, -5);
		System.out.println("5分钟前："+getDateStr(date, pattern));
	}
}
