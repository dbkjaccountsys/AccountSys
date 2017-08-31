package com.dbkj.account.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dic.UserType;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.User;
import com.dbkj.account.model.UserLog;
import com.dbkj.account.util.WebUtil;
import com.jfinal.kit.StrKit;

/**
 * 操作日志相关操作
 * @author DELL
 *
 */
public class LogService {

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
	public void addLog(UserType userType,Integer userId,String ip,String content,Integer operaTypeId,OperaResult operaResult,String exceptionMsg){
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
		Object obj=request.getAttribute(Constant.CURRENT_USER);
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
			addLog(userType, Integer.parseInt(id.toString()), ip, content,operaTypeId,operaResult,exceptionMsg);
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
		if(operaResult==null){
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
		addLog(userType,Integer.parseInt(userId.toString()),ip,content,operaType,operaResult,exceptionMsg);
	}
}
