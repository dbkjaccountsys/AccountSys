package com.dbkj.account.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dto.Result;
import com.dbkj.account.model.Admin;
import com.dbkj.account.vo.InfoFormVo;
import com.dbkj.account.vo.UpdatePwdFormVo;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;

public class PersonalService {
	
	private LogService logService;

	public Result updatePassword(UpdatePwdFormVo formVo,Admin admin,HttpServletRequest request){
		Result result=validate(formVo);
		if(result.isSuccess()){
			String encryptPassword=DigestUtils.md5Hex(formVo.getOldPassword());
			if(encryptPassword.equals(admin.getPassword())){
				admin.setPassword(DigestUtils.md5Hex(formVo.getNewPassword()));
				boolean flag=admin.update();
				result.setSuccess(flag);
				if(logService==null){
					logService=new LogService();
				}
				logService.addLog(request, "修改密码，"+JSON.toJSONString(formVo),
						flag?OperaResult.SUCCESS:OperaResult.FAIL, null);
			}else{
				Res res=I18n.use("zh_CN");
				result.setSuccess(false);
				result.setReason(res.get("update.password.old.password.incorrect"));
			}
			
		}
		return result;
	}
	
	private Result validate(UpdatePwdFormVo formVo){
		Res res=I18n.use("zh_CN");
		Result result=new Result(false);
		if(formVo==null){
			return result;
		}
		if(StrKit.isBlank(formVo.getOldPassword())){
			result.setReason(res.get("update.password.old.password.empty"));
			return result;
		}
		if(StrKit.isBlank(formVo.getNewPassword())){
			result.setReason(res.get("update.password.new.password.empty"));
			return result;
		}
		if(StrKit.isBlank(formVo.getConfirmPassword())){
			result.setReason(res.get("update.password.confirm.password.empty"));
			return result;
		}
		if(!formVo.getNewPassword().equals(formVo.getConfirmPassword())){
			result.setReason(res.get("update.password.confirm.password.not.equal"));
			return result;
		}
		result.setSuccess(true);
		return result;
	}
	
	public Admin getAdmin(long id){
		Admin admin=Admin.dao.findById(id);
		return admin;
	}
	
	
	public boolean updateInfo(InfoFormVo info,long id,HttpServletRequest request){
		if(info==null){
			return false;
		}
		Admin admin=Admin.dao.findById(id);
		admin.setName(info.getName());
		admin.setPhone(info.getPhone());
		admin.setEmail(info.getEmail());
		boolean flag=admin.update();
		if(logService==null){
			logService=new LogService();
		}
		logService.addLog(request, "修改个人信息，"+JSON.toJSONString(admin), 
				flag?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return flag;
	}
}
