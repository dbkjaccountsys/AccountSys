package com.dbkj.account.service;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.config.SqlContext;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dic.ReviewStatus;
import com.dbkj.account.dto.FrontUserDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.Result;
import com.dbkj.account.model.User;
import com.dbkj.account.model.UserInfo;
import com.dbkj.account.util.FileUtil;
import com.dbkj.account.util.WebUtil;
import com.dbkj.account.vo.FrontUserFormVo;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.upload.UploadFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class FrontUserManageService {
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	private AuthorityService authorityService;
	private LogService logService;

	public void getUserPage(Page<FrontUserDto> page,String username,long roleId){
		List<Object> params=new ArrayList<Object>(3);
		Map<String,Object> paraMap=new HashMap<>(1);
		if(!StrKit.isBlank(username)){
			paraMap.put("username",username);
			params.add(username+"%");
		}
		String sql= SqlContext.getSqlByFreeMarker(User.class,"getUserList",paraMap);
		String countSql=SqlContext.getSqlByFreeMarker(User.class,"getCount",paraMap);
		
		long count=Db.queryLong(countSql, params.toArray(new Object[params.size()]));
		page.setRecords(count);
		page.setTotalCount((int)Math.ceil(count/(double)page.getPageSize()));
		
		int limit = (page.getCurrentPage()-1)*page.getPageSize();
		params.add(limit);
		params.add(page.getPageSize());
		
		if(logger.isInfoEnabled()){
			logger.info("分页SQL：{}，查询参数：{}",sql,JSON.toJSON(params));
		}
		List<User> ulist=User.dao.find(sql,params.toArray(new Object[params.size()]));
		List<FrontUserDto> rows=new ArrayList<FrontUserDto>(ulist.size());
		for(User user:ulist){
			FrontUserDto dto=convert2FrontUserDto(user, roleId);
			rows.add(dto);
		}
		page.setData(rows);
	}
	
	public boolean existsPhone(String phone){
		return Db.queryLong(SqlContext.getSqlByFreeMarker(User.class, "isExistsPhone",null),phone)>0;
	}
	
	private FrontUserDto convert2FrontUserDto(Object obj,Long roleId){
		FrontUserDto dto=new FrontUserDto();
		if(obj!=null&&obj instanceof User){
			User user=(User)obj;
			dto.setId(user.getId());
			dto.setUsername(user.getUsername());
			dto.setName(user.getName());
			dto.setPhone(user.getPhone());
			dto.setEmail(user.getEmail());
			dto.setCharge(user.getCharge()!=null?user.getCharge().doubleValue():null);
			dto.setCompanyName(user.getStr("companyname"));
			dto.setContact(user.getStr("contact"));
			dto.setContactPhone(user.getStr("contactphone"));
			dto.setPublicAccount(user.getStr("publicaccount"));
			dto.setAccountName(user.getStr("accountname"));
			dto.setBank(user.getStr("bank"));
			dto.setTaxAccount(user.getStr("taxaccount"));
			
			Integer ispass=user.getInt("ispass");
			String status=null;
			if(ispass==null){
				status="未认证";
			}else if(ReviewStatus.PASS.getCode()==ispass.intValue()){
				status="已认证";
			}else if(ReviewStatus.PENDING.getCode()==ispass.intValue()){
				status="认证中";
			}else if(ReviewStatus.REJECT.getCode()==ispass.intValue()){
				status="认证未通过";
			}
			if(roleId!=null){
				String opera=getOpera(roleId,ispass);
				dto.setOpera(opera);
			}
			dto.setIspass(status);
		}
		return dto;
	}
	
	private String getOpera(long roleId){
		return getOpera(roleId, null);
	}
	
	private String getOpera(long roleId,Integer status){
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
		if(status==null||ReviewStatus.PASS.getCode()!=status){
			//判断用户是否有编辑用户的权限
			if(authorityService.isPermissionOfAdmin(roleId, "/manage/frontUser/edit")){
				jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"edit\\\" href=\\\"javascript:;\\\" title=\\\"编辑\\\"><i class=\\\"fa fa-edit fa-2\\\"></i></a>&nbsp;&nbsp;\";");
			}
		}
		
		//判断用户是否查看删除的权限
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/frontUser/delete")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"delete\\\" href=\\\"javascript:;\\\" title=\\\"删除\\\"><i class=\\\"fa fa-trash fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		jsFunction.append("return content;}");
		return jsFunction.toString();
	}
	
	public boolean addUser(FrontUserFormVo vo,HttpServletRequest request){
		if(vo==null){
			return false;
		}
		final User user=new User();
		user.setUsername(vo.getPhone());
		user.setPassword(DigestUtils.md5Hex(vo.getPassword()));
		user.setName(vo.getName());
		user.setPhone(vo.getPhone());
		user.setEmail(vo.getEmail());
		Date date=new Date();
		user.setCreateTime(date);
		
		final UserInfo userInfo=new UserInfo();
		userInfo.setCompanyname(vo.getCompanyName());
		userInfo.setContact(vo.getContact());
		userInfo.setContactphone(vo.getContactPhone());
		userInfo.setPublicaccount(vo.getPublicAccount());
		userInfo.setAccountname(vo.getAccountName());
		userInfo.setBank(vo.getBank());
		userInfo.setTaxaccount(vo.getTaxAccount());
		userInfo.setCreateTime(date);
		userInfo.setModifyTime(date);
		
		//保存图片
		try {
			String phone=vo.getPhone();
			String licenseImg=saveImg(request, phone, vo.getLicenseFile());
			userInfo.setLicence(licenseImg);
			vo.setLicense(licenseImg);
			String idcardImg=saveImg(request, phone, vo.getIdcardFile());
			userInfo.setIdcard(idcardImg);
			vo.setIdcard(idcardImg);
			String safetyImg=saveImg(request, phone, vo.getSafetyFile());
			userInfo.setSafety(safetyImg);
			vo.setSafety(safetyImg);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		
		boolean result=Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				if(user.save()){
					userInfo.setUserid(user.getId());
					return userInfo.save();
				}
				return false;
			}
		});
		if(logService==null){
			logService=new LogService();
		}
		logService.addLog(request, "添加用户,用户数据："+JSON.toJSONString(vo), 
				result?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return result;
	}
	
	public String saveImg(HttpServletRequest request,String phone,UploadFile uploadFile) throws IOException{
		String rootPath = WebUtil.getRootPath(request);
		String path=rootPath+File.separator+PropKit.get("uploadImage","uploadimages")+File.separator+phone;
		String lastName=uploadFile.getFileName().substring(uploadFile.getFileName().lastIndexOf("."));
		String fileName=System.currentTimeMillis()+"_"+phone+lastName;
		
		File file=new File(path+File.separator+fileName);
		FileUtil.copyFile(uploadFile.getFile(), file);
		uploadFile.getFile().delete();
		return phone+"/"+fileName;
	}
	
	public FrontUserFormVo getUserInfo(Long id){
		FrontUserFormVo vo=new FrontUserFormVo();
		if(id!=null){
			User user=User.dao.findFirst(SqlContext.getSqlByFreeMarker(User.class, "getUserById"),id);
			vo.setId(user.getId());
			vo.setPhone(user.getPhone());
			vo.setName(user.getName());
			vo.setEmail(user.getEmail());
			vo.setCompanyName(user.getStr("companyname"));
			vo.setContact(user.getStr("contact"));
			vo.setContactPhone(user.getStr("contactphone"));
			vo.setPublicAccount(user.getStr("publicaccount"));
			vo.setAccountName(user.getStr("accountname"));
			vo.setBank(user.getStr("bank"));
			vo.setTaxAccount(user.getStr("taxaccount"));
		}
		return vo;
	}
	
	public boolean updateUser(final FrontUserFormVo formVo,final HttpServletRequest request){
		if(formVo==null){
			return false;
		}
		final User user=new User().dao().findById(formVo.getId());
		user.setUsername(formVo.getPhone());
		user.setPassword(DigestUtils.md5Hex(formVo.getPassword()));
		user.setName(formVo.getName());
		user.setPhone(formVo.getPhone());
		user.setEmail(formVo.getEmail());
		final Date date=new Date();
		user.setCreateTime(date);
		
		if(logService==null){
			logService=new LogService();
		}
		
		
		
		boolean result=Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				if(user.update()){
					UserInfo userInfo=UserInfo.dao.findFirst(SqlContext.getSqlByFreeMarker(UserInfo.class, "findByUserId"),formVo.getId());
					if(userInfo==null){
						userInfo=new UserInfo();
						userInfo.setUserid(user.getId());
					}
					userInfo.setCompanyname(formVo.getCompanyName());
					userInfo.setContact(formVo.getContact());
					userInfo.setContactphone(formVo.getContactPhone());
					userInfo.setPublicaccount(formVo.getPublicAccount());
					userInfo.setAccountname(formVo.getAccountName());
					userInfo.setBank(formVo.getBank());
					userInfo.setTaxaccount(formVo.getTaxAccount());
					if(userInfo.getId()==null){
						userInfo.setCreateTime(date);
					}
					userInfo.setModifyTime(date);
					//保存图片
					try {
						String phone=formVo.getPhone();
						UploadFile licenseFile=formVo.getLicenseFile();
						if(licenseFile!=null){
							String licenseImg=saveImg(request, phone, licenseFile);
							userInfo.setLicence(licenseImg);
							formVo.setLicense(licenseImg);
						}
						
						UploadFile idcardFile=formVo.getIdcardFile();
						if(idcardFile!=null){
							String idcardImg=saveImg(request, phone, formVo.getIdcardFile());
							userInfo.setIdcard(idcardImg);
							formVo.setIdcard(idcardImg);
						}
						
						UploadFile safetyFile=formVo.getSafetyFile();
						if(safetyFile!=null){
							String safetyImg=saveImg(request, phone, formVo.getSafetyFile());
							userInfo.setSafety(safetyImg);
							formVo.setSafety(safetyImg);
						}
						
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
						logService.addLog(request, "修改用户,用户数据："+JSON.toJSONString(formVo), 
								OperaResult.EXCEPTION, e.getMessage());
						return false;
					}
					return userInfo.getId()==null?userInfo.save():userInfo.update();
				}
				return false;
			}
		});
		
		logService.addLog(request, "修改用户,用户数据："+JSON.toJSONString(formVo), 
				result?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return result;
	}
	
	public Result<?> deleteUser(Long id,HttpServletRequest request){
		Result result=new Result();
		if(logService==null){
			logService=new LogService();
		}
		if(id==null){
			result.setSuccess(false);
			logService.addLog(request, "将用户id为"+id+"的用户设为不可用", OperaResult.EXCEPTION, "要修改的用户id为null");
			return result;
		}
		final User user=User.dao.findById(id);
		user.setAvailable(false);
		final UserInfo userInfo=UserInfo.dao.findFirst(SqlContext.getSqlByFreeMarker(UserInfo.class, "findByUserId"),user.getId());
		if(userInfo!=null){
			userInfo.setAvailable(false);
		}
		boolean flag=Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				boolean b=user.update();
				if(b&&userInfo!=null){
					return userInfo.update();
				}else{
					return b;
				}
			}
		});
		logService.addLog(request, "将用户id为"+id+"的用户设为不可用", flag?OperaResult.SUCCESS:OperaResult.FAIL, "要修改的用户id为null");
		result.setSuccess(flag);
		return result;
	}
	
	public static void main(String[] args) {
		String sql=SqlContext.getSqlByFreeMarker(User.class, "getUserList").toLowerCase();
		System.out.println(sql);
		int index=sql.indexOf("order");
		String str1=sql.substring(0,index);
		String str2=sql.substring(index);
		System.out.println("str1:"+str1+"\nstr2:"+str2);
	}
}
