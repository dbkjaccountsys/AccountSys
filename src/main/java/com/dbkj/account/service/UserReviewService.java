package com.dbkj.account.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.UserReviewDto;
import com.dbkj.account.model.UserInfo;
import com.dbkj.account.util.SqlUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class UserReviewService {
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	private AuthorityService authorityService;

	/**
	 * 用户审核的数据分页
	 * @param page 
	 * @param fromDate 
	 * @param toDate
	 * @param username
	 */
	public void getPage(Page<UserReviewDto> page,String fromDate,String toDate,String username,long roleId){
		String sql=SqlUtil.getSql(UserInfo.class, "getList");
		int index=sql.lastIndexOf("where")+"where".length();
		String str1=sql.substring(0,index);
		String str2=sql.substring(index);
		
		List<Object> params=new ArrayList<>();
		StringBuilder where=new StringBuilder(80);
		if(!StrKit.isBlank(fromDate)&&!StrKit.isBlank(toDate)){
			where.append(" user_info.modify_time between ? and ? and");
			params.add(fromDate+" 00:00:00");
			params.add(toDate+" 23:59:59");
		}else if(!StrKit.isBlank(fromDate)&&StrKit.isBlank(toDate)){
			where.append(" user_info.modify_time>=? and");
			params.add(fromDate+" 00:00:00");
		}else if(!StrKit.isBlank(toDate)&&StrKit.isBlank(fromDate)){
			where.append(" user_info.modify_time<=? and");
			params.add(toDate+" 23:59:59");
		}
		if(!StrKit.isBlank(username)){
			where.append(" locate(?,`user`.username)>0 and");
			params.add(username.trim());
		}
		
		String whereStr=where.toString();
		Object[] paramsArray=params.toArray(new Object[params.size()]);
		
		//获取总数据条数
		String countSql=SqlUtil.getSql(UserInfo.class, "getCount");
		if(!StrKit.isBlank(whereStr)){
			countSql+=" and"+whereStr.substring(0,whereStr.length()-4);
		}
		
		long count = Db.findFirst(countSql,paramsArray).getLong("count");
		//获取总页码数
		double d=count/(double)page.getPageSize();
		page.setTotalCount(new Double(Math.ceil(d)).intValue());
		
		int limit=(page.getCurrentPage()-1)*page.getPageSize();
		//当limit等于0时，去掉子查询中desc，改为升序排序
		if(limit==0){
			int n=str2.indexOf("desc");
			String s1=str2.substring(0, n);
			String s2=str2.substring(n+"desc".length());
			str2=s1.concat(s2);
		}
		sql=str1.concat(whereStr).concat(str2);
		if(logger.isInfoEnabled()){
			logger.info("分页Sql:{}，分页条件:page:{},fromDate:{},toDate:{},username:{}",sql,page.toString(),fromDate,toDate,username);
		}
		params.add(limit);
		params.add(page.getPageSize());
		paramsArray=params.toArray(new Object[params.size()]);
		
		List<Record> list=Db.find(sql,paramsArray);
		List<UserReviewDto> data=new ArrayList<>(list.size());
		String opera=getOpera(roleId);
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Record r:list){
			UserReviewDto dto=new UserReviewDto();
			dto.setId(r.getLong("id"));
			dto.setUsername(r.getStr("username"));
			dto.setCompanyname(r.getStr("companyname"));
			dto.setContact(r.getStr("contact"));
			dto.setContactphone(r.getStr("contactphone"));
			dto.setModifyTime(sdf.format(r.getDate("modify_time")));
			dto.setOpera(opera);
			data.add(dto);
		}
		page.setData(data);
	}
	
	public void getPage(Page<UserReviewDto> page,long roleId){
		getPage(page,null,null,null,roleId);
	}
	
	private String getOpera(long roleId){
		authorityService=new AuthorityService();
		/**
		 * function(cellvalue, options, rowObject){
		 *		var content="";
		 *		content+="&nbsp;&nbsp;<a class=\"review\" href=\"javascript:;\" title=\"审核\"><i class=\"fa fa-edit fa-2\"></i></a>&nbsp;&nbsp;";
		 *		content+="&nbsp;&nbsp;<a class=\"history\" href=\"javascript:;\" title=\"审核历史\"><i class=\"fa fa-history fa-2\"></i></a>&nbsp;&nbsp;";
		 *		return content;
		 * }
		 */
		StringBuilder jsFunction=new StringBuilder("function(cellvalue, options, rowObject){");
		jsFunction.append("var content=\"\";");
		//判断用户是否有审核用户的权限
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/userReview/userinfo")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"review\\\" href=\\\"javascript:;\\\" title=\\\"审核\\\"><i class=\\\"fa fa-edit fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		//判断用户是否查看审核历史的权限
		if(authorityService.isPermissionOfAdmin(roleId, "/user/delete")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"history\\\" href=\\\"javascript:;\\\" title=\\\"审核历史\\\"><i class=\\\"fa fa-history fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		jsFunction.append("return content;}");
		return jsFunction.toString();
	}
	
	
	public void getUserInfo(long id){
		
	}
	
	public static void main(String[] args) {
		String sql="ispass=0 and user_info.available=1 order by user_info.modify_time desc limit 0,1) order by t1.modify_time desc limit 20";
		int index=sql.indexOf("desc");
		System.out.println(sql.substring(0, index));
		System.out.println(sql.substring(index+4));
	}
}
