package com.dbkj.account.service;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.config.SqlContext;
import com.dbkj.account.dic.Constant;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dto.AdminRoleDto;
import com.dbkj.account.dto.AuthNode;
import com.dbkj.account.dto.Page;
import com.dbkj.account.model.AdminAuth;
import com.dbkj.account.model.AdminRole;
import com.dbkj.account.model.AdminRoleAuth;
import com.dbkj.account.util.CommonUtil;
import com.dbkj.account.vo.RoleFormVo;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminRoleManageService {
	
	private AuthorityService authorityService;
	private LogService logService;
	private final Logger logger=LoggerFactory.getLogger(this.getClass());
	
	public void getRolePage(Page<AdminRoleDto> page,String roleName,Long roleId){
		List<Object> params=new ArrayList<Object>(3);
		Map<String,Object> paraMap=new HashMap<>();
		if(!StrKit.isBlank(roleName)){
			paraMap.put("roleName",roleName);
			roleName="%"+roleName+"%";
			params.add(roleName);
		}
		String sql= SqlContext.getSqlByFreeMarker(AdminRole.class,"getPage",paraMap);
		String countSql=SqlContext.getSqlByFreeMarker(AdminRole.class,"getCount",paraMap);
		
		long count=Db.queryLong(countSql, params.toArray(new Object[params.size()]));
		page.setRecords(count);
		page.setTotalCount((int)Math.ceil(count/(double)page.getPageSize()));
		
		int limit = (page.getCurrentPage()-1)*page.getPageSize();
		params.add(limit);
		params.add(page.getPageSize());
		
		if(logger.isInfoEnabled()){
			logger.info("分页SQL：{}，查询参数：{}",sql,JSON.toJSON(params));
		}
		
		List<AdminRole> list=AdminRole.dao.find(sql, params.toArray(new Object[params.size()]));
		List<AdminRoleDto> rows=new ArrayList<AdminRoleDto>(list.size());
		String opera=getOpera(roleId);
		for(AdminRole adminRole:list){
			AdminRoleDto dto=convert2AdminRoleDto(adminRole, opera);
			rows.add(dto);
		}
		page.setData(rows);
	}
	
	private AdminRoleDto convert2AdminRoleDto(AdminRole adminRole,String opera){
		AdminRoleDto dto=new AdminRoleDto();
		if(adminRole!=null){
			dto.setId(adminRole.getId());
			dto.setRoleName(adminRole.getRolename());
			dto.setDesc(adminRole.getRemark());
			dto.setOpera(opera);
		}
		return dto;
	}
	
	private String getOpera(long roleId){
		authorityService=new AuthorityService();
		/**
		 * function(cellvalue, options, rowObject){
		 *		var content="";
		 *		content+="&nbsp;&nbsp;<a class=\"edit\" href=\"javascript:;\" title=\"编辑\"><i class=\"fa fa-edit fa-2\"></i></a>&nbsp;&nbsp;";
		 *		return content;
		 * }
		 */
		StringBuilder jsFunction=new StringBuilder("function(cellvalue, options, rowObject){");
		jsFunction.append("var content=\"\";");
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/adminRole/edit")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"edit\\\" href=\\\"javascript:;\\\" title=\\\"编辑\\\"><i class=\\\"fa fa-edit fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		
		jsFunction.append("return content;}");
		return jsFunction.toString();
	}
	
	public List<AuthNode> getAdminAuths(){
		List<AuthNode> alist=new ArrayList<AuthNode>();
		List<AdminAuth> list=AdminAuth.dao.find(SqlContext.getSqlByFreeMarker(AdminAuth.class, "getList",null));
		for(AdminAuth auth:list){
			//根节点
			if(auth.getParentId()==0L){
				AuthNode node=new AuthNode();
				node.setId(auth.getId());
				node.setName(auth.getAuthName());
				getChildren(node, list);
				alist.add(node);
			}
		}
		return alist;
	}
	
	private void getChildren(AuthNode node,List<AdminAuth> list){
		for(AdminAuth auth:list){
			Long parentId=auth.getParentId();
			if(parentId!=null&&parentId.longValue()==node.getId().longValue()){
				AuthNode authNode=new AuthNode();
				authNode.setId(auth.getId());
				authNode.setName(auth.getAuthName());
				authNode.setParent(node);
				
				List<AuthNode> children=node.getChildren();
				if(children==null){
					children=new ArrayList<AuthNode>();
					node.setChildren(children);
				}
				children.add(authNode);
				getChildren(authNode, list);
			}
		}
	}
	
	public List<AuthNode> getAdminAuths(final Long roleId){
		return CacheKit.get(Constant.COMMON_CACHE_KEY, "admin_auths_"+roleId, new IDataLoader() {
			
			public Object load() {
				List<AuthNode> list=getAdminAuths();
				if(roleId!=null){
					List<AdminRoleAuth> authList=AdminRoleAuth.dao.find(SqlContext.getSqlByFreeMarker(AdminRoleAuth.class,
							"getRoleAuths",null),roleId);
					for(AdminRoleAuth roleAuth:authList){
						isChecked(roleAuth.getAuthId(), list);
					}
				}
				return list;
			}
		});
		
	}
	
	private void isChecked(long id,List<AuthNode> authList){
		for(AuthNode auth:authList){
			if(auth.getId()!=null&&auth.getId().longValue()==id){
				auth.setChecked(true);
				//子节点选中，父节点一定是选中的
				AuthNode parent=auth.getParent();
				if(parent!=null){
					parent.setChecked(true);
				}
			}
			if(auth.getChildren()!=null&&auth.getChildren().size()>0){
				isChecked(id, auth.getChildren());
			}
		}
	}
	
	public boolean isExistRoleName(String roleName){
		return AdminRole.dao.findFirst(SqlContext.getSqlByFreeMarker(AdminRole.class,
				"isExistByRoleName",null),roleName)!=null;
	}

	public boolean isExistRoleName(String roleName,Long id){
		if(StrKit.isBlank(roleName)){
			return false;
		}
		if(id!=null){
			return AdminRole.dao.findFirst(SqlContext.getSqlByFreeMarker(AdminRole.class,
					"isExistByRoleNameAndId",null),roleName,id)!=null;
		}else{
			return isExistRoleName(roleName);
		}
	}
	
	public boolean addRole(final RoleFormVo vo,HttpServletRequest request){
		if(vo==null){
			return false;
		}
		final AdminRole role=new AdminRole();
		role.setRolename(vo.getRoleName());
		role.setRemark(vo.getDesc());
		
		boolean result=Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				if(role.save()){
					String[] arr=vo.getOperaAuth().split(",");
					List<AdminRoleAuth> list=new ArrayList<AdminRoleAuth>(arr.length);
					for(int i=0;i<arr.length;i++){
						AdminRoleAuth a=new AdminRoleAuth();
						a.setAuthId(Long.parseLong(arr[i]));
						a.setRoleId(role.getId());
						list.add(a);
					}
					int[] rArr=Db.batchSave(list, 100);
					return CommonUtil.sum(rArr)==arr.length;
				}
				return false;
			}
		});
		if(logService==null){
			logService=new LogService();
		}
		logService.addLog(request, "添加角色："+JSON.toJSONString(vo), result?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return result;
	}
	
	public RoleFormVo getAdminRole(Long id){
		RoleFormVo vo=new RoleFormVo();
		AdminRole adminRole=AdminRole.dao.findById(id);
		if(adminRole!=null){
			vo.setId(adminRole.getId());
			vo.setRoleName(adminRole.getRolename());
			vo.setDesc(adminRole.getRemark());
		}
		return vo;
	}
	
	@SuppressWarnings("Since15")
	private String operaAuths(long roleId){
		List<AdminRoleAuth> authList=AdminRoleAuth.dao.find(SqlContext.getSqlByFreeMarker(AdminRoleAuth.class,
				"getRoleAuths",null),roleId);
		String[] arr=new String[authList.size()];
		for(int i=0;i<arr.length;i++){
			arr[i]=authList.get(i).getAuthId().toString();
		}
		return String.join(",", arr);

	}
	
	public boolean updateAdminRole(final RoleFormVo vo,HttpServletRequest request){
		if(vo==null){
			return false;
		}
		final AdminRole adminRole=AdminRole.dao.findById(vo.getId());
		if(adminRole==null){
			return false;
		}
		String old=JSON.toJSONString(adminRole);
		String oldAuth=operaAuths(adminRole.getId());
		adminRole.setRolename(vo.getRoleName());
		adminRole.setRemark(vo.getDesc());
		boolean result=Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				if(adminRole.update()){
					//先删除要修改角色的所有关联的权限，然后再重新添加
					Db.update(SqlContext.getSqlByFreeMarker(AdminRoleAuth.class, "deleteByRoleId",null), adminRole.getId());
					String[] arr=vo.getOperaAuth().split(",");
					List<AdminRoleAuth> list=new ArrayList<AdminRoleAuth>(arr.length);
					for(int i=0;i<arr.length;i++){
						AdminRoleAuth a=new AdminRoleAuth();
						a.setAuthId(Long.parseLong(arr[i]));
						a.setRoleId(adminRole.getId());
						list.add(a);
					}
					int[] rArr=Db.batchSave(list, 100);
					return CommonUtil.sum(rArr)==arr.length;
				}
				return false;
			}
		});
		if(logService==null){
			logService=new LogService();
		}
		logService.addLog(request, "修改角色："+old+"，修改前的角色权限id为："+oldAuth+"，修改后为："+JSON.toJSONString(vo),
				result?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return result;
	}
}
