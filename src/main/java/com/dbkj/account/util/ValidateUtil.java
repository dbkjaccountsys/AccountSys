package com.dbkj.account.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfinal.kit.StrKit;

public class ValidateUtil {

	/**
	 * 判断字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		if(StrKit.isBlank(str)){
			return false;
		}
		Pattern pattern=Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	
	 /**
     * 验证手机号码
     * @param str
     * @return
     */
    public static boolean validateMobilePhone(String str){
        String regex="^1[3|4|5|7|8]\\d{9}$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(str);
        return matcher.matches();
    }
    
    /**
     * 验证座机号码
     * @param str
     * @return
     */
    public static boolean validatePhone(String str){
    	 String regex="^([0-9]{3,4})?(-)?([0-9]{7,9}|[0-9]{5})$";
         Pattern pattern=Pattern.compile(regex);
         Matcher matcher=pattern.matcher(str);
         return matcher.matches();
    }
    
    /**
     * 
     * @return
     */
    public static boolean validatePhoneAndMobileNum(String str){
    	return validatePhone(str)||validateMobilePhone(str);
    }
    
    /**
     * 验证电子邮箱
     * @param str
     * @return
     */
    public static boolean validateEmail(String str){
        String regex="^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(str);
        return matcher.matches();
    }
    
    /**
     * 验证是否包含特殊字符�?
     * @param value
     * @return
     */
    public static boolean validateSpecialString(String value){
        String regex="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#�?%…�??&*（）—�??+|{}【�?��?�；：�?��?��?��?�，、？]";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(value);
        return matcher.find();
    }
    
    /**
     * 判断日期字符串是否指定格�?
     * @param dateStr
     * @param format
     * @return
     */
    public static boolean validateDateFormat(String dateStr,String format){
    	SimpleDateFormat sdf=new SimpleDateFormat(format);
    	try {
			Date date = sdf.parse(dateStr);
			String newDateStr=sdf.format(date);
			return dateStr.equals(newDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    /**
     * 验证有n位小数的正实�?
     * @param str
     * @param n
     * @return
     */
    public static boolean validatePositivRealNumber(String str,int n){
    	String regex="^[0-9]+(.[0-9]{"+n+"})?$";
    	Pattern pattern=Pattern.compile(regex);
    	Matcher matcher=pattern.matcher(str);
    	return matcher.matches();
    }
    
    /**
     * 验证是否是有效数字格式，包括正负数�?�小�?
     * @param str
     * @return
     */
    public static boolean isDigital(String str){
    	String regex="^[\\+-]?\\d*[\\.\\d]{0,}$";
    	Pattern pattern=Pattern.compile(regex);
    	Matcher matcher=pattern.matcher(str);
    	return matcher.matches();
    }
    
    /**
     * 验证密码格式是否正确
     * @param str
     * @return
     */
    public static boolean validatePassword(String str){
    	String regex="^[0-9a-zA-Z!@#$^]{6,18}$";
    	Pattern pattern=Pattern.compile(regex);
    	Matcher matcher=pattern.matcher(str);
    	return matcher.matches();
    }
    
    
    public static void main(String[] args) {
		System.out.println(isDigital("60.00"));
	}
}
