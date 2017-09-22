package com.dbkj.account.model;

import com.dbkj.account.config.SqlContext;
import com.dbkj.account.dic.Constant;
import com.dbkj.account.model.base.BaseUserLog;
import com.dbkj.account.util.DateUtil;
import com.jfinal.plugin.activerecord.Db;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class UserLog extends BaseUserLog<UserLog> {
	public static final UserLog dao = new UserLog().dao();
	
	public void add(UserLog userLog){
		createTable();
		String sql= SqlContext.getSqlByFreeMarker(UserLog.class,"add");
		String dateStr=DateUtil.getDateStr("yyyyMM");
		String newTableName=Constant.BASE_LOG_TABLE+"_"+dateStr;
		sql=sql.replace(Constant.BASE_LOG_TABLE, newTableName);
		Db.update(sql, userLog.getUsertype(),userLog.getUserid(),userLog.getTime(),userLog.getIp(),userLog.getContent(),
				userLog.getOperaType(),userLog.getOperaResult(),userLog.getExceptionMsg());
	}
	
	public void createTable(){
		//按照月份分表
		String dateStr=DateUtil.getDateStr("yyyyMM");
		String sql=SqlContext.getSqlByFreeMarker(UserLog.class, "createTable");
		String str1=sql.substring(0,sql.indexOf(Constant.BASE_LOG_TABLE)+Constant.BASE_LOG_TABLE.length())+"_"+dateStr;
		String str2=sql.substring(sql.indexOf(Constant.BASE_LOG_TABLE)+Constant.BASE_LOG_TABLE.length());
		Db.update(str1+str2);		
	}
	
	public static void main(String[] args) {
		String sql=SqlContext.getSqlByFreeMarker(UserLog.class, "createTable");
		sql=sql.replace("user_log", "user_log_201708");
		System.out.println(sql);
	}
}
