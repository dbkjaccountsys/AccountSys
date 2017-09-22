package com.dbkj.account.service;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.config.SqlContext;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dto.AdminDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.Result;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.AdminRole;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackUserManageService {
	
	private AuthorityService authorityService;
	private LogService logService;
	private Logger logger=LoggerFactory.getLogger(this.getClass());

	public void getAdminPage(Page<AdminDto> page,String username,Admin current){
		List<Object> params=new ArrayList<Object>(3);
		Map<String,Object> paraMap=null;
		if(!StrKit.isBlank(username)){
			paraMap=new HashMap<>();
			paraMap.put("username",username);
			username="%"+username+"%";
			params.add(username);
		}
		String sql= SqlContext.getSqlByFreeMarker(Admin.class,"getPageList",paraMap);
		String countSql=SqlContext.getSqlByFreeMarker(Admin.class,"getCount",paraMap);
		
		long count=Db.queryLong(countSql, params.toArray(new Object[params.size()]));
		page.setRecords(count);
		page.setTotalCount((int)Math.ceil(count/(double)page.getPageSize()));
		
		int limit = (page.getCurrentPage()-1)*page.getPageSize();
		params.add(limit);
		params.add(page.getPageSize());
		
		if(logger.isInfoEnabled()){
			logger.info("分页SQL：{}，查询参数：{}",sql,JSON.toJSON(params));
		}
		
		List<Admin> list=Admin.dao.find(sql,params.toArray(new Object[params.size()]));
		List<AdminDto> rows=new ArrayList<AdminDto>();
		for(Admin admin:list){
			AdminDto dto=convert2AdminDto(admin, current);
			rows.add(dto);
		}
		page.setData(rows);
	}
	
	private AdminDto convert2AdminDto(Admin admin,Admin current){
		AdminDto dto=new AdminDto();
		if(admin!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dto.setId(admin.getId());
			dto.setUsername(admin.getUsername());
			dto.setName(admin.getName());
			dto.setPhone(admin.getPhone());
			dto.setEmail(admin.getEmail());
			dto.setRoleId(admin.getRoleId());
			dto.setRoleName(admin.getStr("remark"));
			dto.setCreateTime(sdf.format(admin.getCreateTime()));
			String opera=getOpera(current.getRoleId(), admin.getId(), current.getId());
			dto.setOpera(opera);
		}
		return dto;
	}
	
	/**
	 * 获取操作
	 * @param roleId 当前登录用户的角色id
	 * @param uid 要操作的数据的id
	 * @param cid 当前登陆用户的id
	 * @return
	 */
	private String getOpera(long roleId,long uid,long cid){
		authorityService=new AuthorityService();
		/**
		 * function(cellvalue, options, rowObject){
		 *		var content="";
		 *		content+="&nbsp;&nbsp;<a class=\"edit\" href=\"javascript:;\" title=\"编辑\"><i class=\"fa fa-edit fa-2\"></i></a>&nbsp;&nbsp;";
		 *		content+="&nbsp;&nbsp;<a class=\"cancel\" href=\"javascript:;\" title=\"删除\"><i class=\"fa fa-times fa-2\"></i></a>&nbsp;&nbsp;";
		 *		return content;
		 * }
		 */
		StringBuilder jsFunction=new StringBuilder("function(cellvalue, options, rowObject){");
		jsFunction.append("var content=\"\";");
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/backUser/edit")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"edit\\\" href=\\\"javascript:;\\\" title=\\\"编辑\\\"><i class=\\\"fa fa-edit fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		
		if(uid!=cid){
			//判断用户是否查看删除的权限
			if(authorityService.isPermissionOfAdmin(roleId, "/manage/backUser/delete")){
				jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"delete\\\" href=\\\"javascript:;\\\" title=\\\"删除\\\"><i class=\\\"fa fa-trash fa-2\\\"></i></a>&nbsp;&nbsp;\";");
			}
		}
		
		jsFunction.append("return content;}");
		return jsFunction.toString();
	}
	
	public List<AdminRole> getAdminRoleList(){
		List<AdminRole> list=AdminRole.dao.find(SqlContext.getSqlByFreeMarker(AdminRole.class, "getList",null));
		return list;
	}
	
	public boolean existsUsername(String username){
		return Admin.dao.findFirst(SqlContext.getSqlByFreeMarker(Admin.class, "getIdByUsername",null),username)!=null;
	}
	
	public boolean addUser(AdminDto dto,HttpServletRequest request){
		if(dto==null){
			return false;
		}
		Date date=new Date();
		Admin admin=new Admin();
		admin.setUsername(dto.getUsername());
		admin.setPassword(DigestUtils.md5Hex(dto.getPassword()));
		admin.setName(dto.getName());
		admin.setPhone(dto.getPhone());
		admin.setEmail(dto.getEmail());
		admin.setRoleId(dto.getRoleId());
		admin.setCreateTime(date);
		
		boolean result=admin.save();
		if(logService==null){
			logService=new LogService();
		}
		logService.addLog(request, "添加后台用户："+JSON.toJSONString(dto), 
				result?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return result;
	}
	
	public AdminDto getUser(Long id){
		AdminDto dto=new AdminDto();
		if(id!=null){
			Admin admin=Admin.dao.findById(id);
			dto.setId(admin.getId());
			dto.setUsername(admin.getUsername());
			dto.setName(admin.getName());
			dto.setPhone(admin.getPhone());
			dto.setEmail(admin.getEmail());
			dto.setRoleId(admin.getRoleId());
		}
		return dto;
	}
	
	public boolean updateUser(AdminDto dto,HttpServletRequest request){
		if(dto==null){
			return false;
		}
		Admin admin=Admin.dao.findById(dto.getId());
		String before=JSON.toJSONString(admin);
		if(admin==null){
			return false;
		}
		admin.setPassword(DigestUtils.md5Hex(dto.getPassword()));
		admin.setName(dto.getName());
		admin.setPhone(dto.getPhone());
		admin.setEmail(dto.getEmail());
		admin.setRoleId(dto.getRoleId());
		boolean result=admin.update();
		if(logService==null){
			logService=new LogService();
		}
		logService.addLog(request, "修改后台用户："+before+",修改后："+JSON.toJSONString(admin), 
				result?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return result;
	}
	
	public Result<?> deleteUser(Long id,HttpServletRequest request){
		Result result=new Result();
		if(id==null){
			result.setSuccess(false);
			return result;
		}
		Admin admin=Admin.dao.findById(id);
		if(admin==null){
			result.setSuccess(false);
			result.setReason("不存在该用户");
			return result;
		}
		admin.setAvailable(false);
		boolean b=admin.update();
		if(logService==null){
			logService=new LogService();
		}
		logService.addLog(request, "删除后台用户："+JSON.toJSONString(admin), b?OperaResult.SUCCESS:OperaResult.FAIL, null);
		result.setSuccess(b);
		return result;
	}
}
