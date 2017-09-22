package com.dbkj.account.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbkj.account.config.SqlContext;
import com.dbkj.account.model.OperaType;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class OperaTypeService {

	//存储操作url与操作类型映射关系
	private static final Map<String, Integer> OPERA_TYPE_MAP=new HashMap<String, Integer>();
	
	public static void init(){
		List<Record> list = Db.find(SqlContext.getSqlByFreeMarker(OperaType.class, "getOperaList"));
		for(Record r:list){
			OperaTypeService.OPERA_TYPE_MAP.put(r.getStr("action_url"), r.getInt("id"));
		}
	}
	
	/**
	 * 根据action获取操作类型
	 * @param actionUrl
	 * @return
	 */
	public static Integer getOperaType(String actionUrl){
		return OperaTypeService.OPERA_TYPE_MAP.get(actionUrl);
	}
}
