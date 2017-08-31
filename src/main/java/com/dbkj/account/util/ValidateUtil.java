package com.dbkj.account.util;

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
     * 验证是否包含特殊字符串
     * @param value
     * @return
     */
    public static boolean validateSpecialString(String value){
        String regex="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(value);
        return matcher.find();
    }
}
