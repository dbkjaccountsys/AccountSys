package com.dbkj.account.dic;

/**
 * 公用常量
 * @author 钦春�?
 *
 */
public interface Constant {

	/**
	 * 当前登录用户的session key
	 */
	String CURRENT_USER="current_user";
	/**
	 * 日志模板表名
	 */
	String BASE_LOG_TABLE="user_log";
	
	String SUCCESS="3000";
	String FAIL="3001";
	/**
	 * 通用cache key
	 */
	String COMMON_CACHE_KEY="otherCache";
}
