package com.dbkj.account.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.dic.Constant;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dic.UserType;
import com.dbkj.account.dto.LogDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.OperaType;
import com.dbkj.account.model.User;
import com.dbkj.account.model.UserLog;
import com.dbkj.account.util.SqlUtil;
import com.dbkj.account.util.WebUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * 操作日志相关操作
 * @author DELL
 *
 */
public class LogService {
	
	private final Logger logger=LoggerFactory.getLogger(this.getClass());

	/**
	 * 添加操作日志
	 * @param userType 用户类型 @see com.qin.account.dic.UserType
	 * @param userId 操作用户ID
	 * @param ip 操作IP
	 * @param content 操作内容
	 * @param operaTypeId 操作类型
	 * @param operaResult 操作结果 @see com.qin.account.dic.OperaResult
	 * @param exceptionMsg 操作异常信息
	 */
	public void addLog(UserType userType,Long userId,String ip,String content,Integer operaTypeId,OperaResult operaResult,String exceptionMsg){
		if(userType==null||userId==null||StrKit.isBlank(ip)||StrKit.isBlank(content)||operaTypeId==null||operaResult==null){
			throw new IllegalArgumentException("添加的日志参数不能为空");
		}
		UserLog userLog=new UserLog();
		userLog.setUsertype(userType.getValue());
		userLog.setUserid(userId);
		userLog.setTime(new Date());
		userLog.setIp(ip);
		userLog.setContent(content);
		userLog.setOperaType(operaTypeId);
		userLog.setOperaResult(operaResult.getValue());
		userLog.setExceptionMsg(exceptionMsg);
		UserLog.dao.add(userLog);
	}
	
	/**
	 * 添加操作日志
	 * @param request
	 * @param content
	 */
	public void addLog(HttpServletRequest request,String content,Integer operaTypeId,OperaResult operaResult,String exceptionMsg){
		Object obj=request.getSession().getAttribute(Constant.CURRENT_USER);
		if(obj!=null){
			Long id=null;
			UserType userType=null;
			if(obj instanceof Admin){
				Admin admin=(Admin) obj;
				id=admin.getId();
				userType=UserType.ADMIN;
			}else if(obj instanceof User){
				User user=(User) obj;
				id=user.getId();
				userType=UserType.USER;
			}
			String ip=WebUtil.getIp(request);
			addLog(userType, id, ip, content,operaTypeId,operaResult,exceptionMsg);
		}
	}
	
	/**
	 * 添加操作日志
	 * @param request 当前请求
	 * @param content 操作内容
	 * @param operaResult 操作结果 @see com.dbkj.account.dic.OperaResult
	 * @param exceptionMsg 异常信息
	 */
	public void addLog(HttpServletRequest request,String content,OperaResult operaResult,String exceptionMsg){
		String action=WebUtil.getAction(request);
		Integer operaType=OperaTypeService.getOperaType(action);
		if(operaType==null){
			throw new RuntimeException("找不到对应操作类型");
		}
		addLog(request, content, operaType, operaResult, exceptionMsg);
	}
	
	public void addLog(HttpServletRequest request,UserType userType,Long userId,String content,OperaResult operaResult,String exceptionMsg){
		String action=WebUtil.getAction(request);
		Integer operaType=OperaTypeService.getOperaType(action);
		if(operaResult==null){
			throw new RuntimeException("找不到对应操作类型");
		}
		String ip=WebUtil.getIp(request);
		addLog(userType,userId,ip,content,operaType,operaResult,exceptionMsg);
	}
	
	/**
	 * 获取日志分页数据
	 * @param page 分页
	 * @param month 月份
	 * @param userType 用户类型
	 * @param operaResult 操作结果
	 * @param userId 用户id
	 * @param startTime 
	 * @param endTime
	 */
	public void getPage(Page<LogDto> page,String month,int userType,int operaResult,String username,String startTime,String endTime,int operaType){
		String sql=SqlUtil.getSql(UserLog.class, "getPage").toLowerCase();
		String countSql=SqlUtil.getSql(UserLog.class, "getCount").toLowerCase();
		
		if(StrKit.isBlank(month)){
			SimpleDateFormat monthFormat=new SimpleDateFormat("yyyyMM");
			Date date=new Date();
			month=monthFormat.format(date);
		}
		String oldTableName="user_log";
		String newTableName=oldTableName+"_"+month;
		sql=sql.replace(oldTableName, newTableName);
		countSql=countSql.replace(oldTableName, newTableName);

		List<Object> params=new ArrayList<Object>(10);
		params.add(userType);
		params.add(operaResult);
		
		StringBuilder where=new StringBuilder();
		if(!StrKit.isBlank(username)){
			username=username.trim();
			StringBuilder sb=new StringBuilder("(");
			if(userType==UserType.ADMIN.getValue()){
				List<Admin> list=Admin.dao.find(SqlUtil.getSql(Admin.class, "getListByUsername"),username+"%");
				if(list.size()>0){
					for(int i=0,size=list.size();i<size;i++){
						sb.append(list.get(i).getId());
						if(i!=size-1){
							sb.append(",");
						}
					}
				}else{
					sb.append("0");
				}
			}else{
				List<User> list=User.dao.find(SqlUtil.getSql(User.class, "getListByUsername"),username+"%");
				if(list.size()>0){
					for(int i=0,size=list.size();i<size;i++){
						sb.append(list.get(i).getId());
						if(i!=size-1){
							sb.append(",");
						}
					}
				}else{
					sb.append("0");
				}
			}
			sb.append(") ");
			where.append(" and userid in ").append(sb);
		}
		if(!StrKit.isBlank(startTime)&&!StrKit.isBlank(endTime)){
			where.append(" and time between ? and ? ");
			params.add(startTime);
			params.add(endTime);
		}else if(!StrKit.isBlank(startTime)&&StrKit.isBlank(endTime)){
			where.append(" and time>=? ");
			params.add(startTime);
		}else if(StrKit.isBlank(startTime)&&!StrKit.isBlank(endTime)){
			where.append(" and time<=? ");
			params.add(endTime);
		}
		
		if(operaType!=0){
			where.append(" and opera_type=? ");
			params.add(operaType);
		}
		
		long count=Db.queryLong(countSql+where, params.toArray(new Object[params.size()]));
		page.setRecords(count);
		page.setTotalCount((int)Math.ceil(count/(double)page.getPageSize()));
		
		int limit = (page.getCurrentPage()-1)*page.getPageSize();
		params.add(limit);
		params.add(page.getPageSize());
		
		if(where.length()>0){
			int index=sql.indexOf("order");
			String str1=sql.substring(0, index);
			String str2=sql.substring(index);
			sql=str1+where.toString()+str2;
		}
		if(logger.isInfoEnabled()){
			logger.info("分页SQL：{}，查询参数：{}",sql,JSON.toJSON(params));
		}
		
		List<UserLog> logList=UserLog.dao.find(sql,params.toArray(new Object[params.size()]));
		List<LogDto> rows=new ArrayList<LogDto>(logList.size());
		for(UserLog log:logList){
			rows.add(convert2LogDto(log));
		}
		page.setData(rows);
	}
	
	private LogDto convert2LogDto(UserLog log){
		LogDto dto=new LogDto();
		if(log!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dto.setId(log.getId());
			UserType type=UserType.valueOf(log.getUsertype());
			if(type!=null){
				dto.setUserType(type.getDesc());
			}
			dto.setUserId(log.getUserid());
			dto.setUsername(log.getStr("username"));
			dto.setTime(sdf.format(log.getTime()));
			dto.setIp(log.getIp());
			dto.setContent(log.getContent());
			dto.setOperaType(log.getStr("desc"));
			OperaResult result=OperaResult.valueOf(log.getOperaResult());
			if(result!=null){
				dto.setOperaResult(result.getDesc());
			}
			dto.setExceptionMsg(log.getExceptionMsg());
		}
		return dto;
	}
	
	/**
	 * 获取有日志记录的月份
	 * @return
	 */
	public List<Integer> getLogMonth(){
		return CacheKit.get(Constant.COMMON_CACHE_KEY, "months_cache", new IDataLoader() {
			
			public Object load() {
				String sql="show tables";
				List<Record> list = Db.find(sql);
				List<Integer> months=new ArrayList<Integer>();
				for(Record record:list){
					String tableName = record.getStr("Tables_in_dbtec");
					String tablePrefix="user_log";
					if(tableName.startsWith(tablePrefix)&&!tablePrefix.equals(tableName)){
						int index=tableName.lastIndexOf("_")+1;
						Integer month=Integer.parseInt(tableName.substring(index));
						months.add(month);
					}
				}
				//降序排序
				Collections.reverse(months);
				return months;
			}
		});
	}
	
	/**
	 * 获取用户类型
	 * @return
	 */
	public List<UserType> getUserTypes(){
		List<UserType> list=new ArrayList<UserType>(2);
		list.add(UserType.ADMIN);
		list.add(UserType.USER);
		return list;
	}
	
	/**
	 * 获取操作结果
	 * @return
	 */
	public List<OperaResult> getOperaResults(){
		List<OperaResult> list=new ArrayList<OperaResult>(3);
		list.add(OperaResult.SUCCESS);
		list.add(OperaResult.FAIL);
		list.add(OperaResult.EXCEPTION);
		return list;
	}
	
	public List<OperaType> getOperaTypes(){
		List<OperaType> list = OperaType.dao.find(SqlUtil.getSql(OperaType.class, "getAll"));
		OperaType df=new OperaType();
		df.setId(0L);
		df.setDesc("--请选择--");
		list.add(0, df);
		return list;
	}
}
