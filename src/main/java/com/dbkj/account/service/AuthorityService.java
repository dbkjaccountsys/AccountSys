package com.dbkj.account.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dbkj.account.model.Action;
import com.dbkj.account.util.SqlUtil;
import com.dbkj.account.util.WebUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class AuthorityService {
	
	private static final String ADMIN_AUTH_CACHE_KEY="adminAuthCache";
	private static final String USER_AUTH_CACHE_KEY="userAuthCache";

	/**
	 * 根据角色id判断是否action的执行权限
	 * @param roleId admin的用户角色
	 * @param action 操作的action
	 * @return
	 */
	public boolean isPermissionOfAdmin(final Long roleId,String action){
		List<Record> list =CacheKit.get(AuthorityService.ADMIN_AUTH_CACHE_KEY, "role_id_"+roleId, new IDataLoader() {
			
			public Object load() {
				return Db.find(SqlUtil.getSql(Action.class,"getActionsUrlByAdminRoleId"),roleId,roleId);
			}
		});
		for(Record r:list){
			String actionUrl=r.getStr("action_url");
			if(!StrKit.isBlank(actionUrl)&&actionUrl.equals(action)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isPermissionOfAdmin(Long roleId,HttpServletRequest request){
		String url=WebUtil.getAction(request);
		return isPermissionOfAdmin(roleId, url);
	}
	
	/**
	 * 根据角色id判断是否action的执行权限
	 * @param roleId user的用户角色id
	 * @param action 操作的action
	 * @return
	 */
	public boolean isPermissionOfUser(final Long roleId,String action){
		List<Record> list =CacheKit.get(AuthorityService.USER_AUTH_CACHE_KEY, "role_id_"+roleId, new IDataLoader() {
			
			public Object load() {
				return Db.find(SqlUtil.getSql(Action.class,"getActionsUrlByUserRoleId"),roleId);
			}
		});
		for(Record r:list){
			String actionUrl=r.getStr("action_url");
			if(!StrKit.isBlank(actionUrl)&&actionUrl.equals(action)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isPermissionOfUser(Long roleId,HttpServletRequest request){
		String url=WebUtil.getAction(request);
		return isPermissionOfUser(roleId, url);
	}
}
