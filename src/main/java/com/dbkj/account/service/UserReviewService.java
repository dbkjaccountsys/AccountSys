package com.dbkj.account.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dbkj.account.config.SqlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dic.ReviewStatus;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.UserReviewDto;
import com.dbkj.account.model.User;
import com.dbkj.account.model.UserInfo;
import com.dbkj.account.model.UserInfoHistory;
import com.dbkj.account.model.UserMail;
import com.dbkj.account.util.FileUtil;
import com.dbkj.account.util.RandomUtil;
import com.dbkj.account.util.WebUtil;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class UserReviewService {
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	private LogService logService=new LogService();
	
	private AuthorityService authorityService;

	/**
	 * 用户审核的数据分�?
	 * @param page 
	 * @param fromDate 
	 * @param toDate
	 * @param username
	 */
	public void getPage(Page<UserReviewDto> page,String fromDate,String toDate,String username,long roleId){
		String sql=SqlContext.getSqlByFreeMarker(UserInfo.class, "getList").toLowerCase();
		int index=sql.lastIndexOf("where")+"where".length();
		String str1=sql.substring(0,index);
		String str2=sql.substring(index);
		
		List<Object> params=new ArrayList<Object>();
		StringBuilder where=new StringBuilder(80);
		
		String start=" 00:00:00";
		String end=" 23:59:59";
		if(!StrKit.isBlank(fromDate)&&!StrKit.isBlank(toDate)){
			where.append(" user_info.modify_time between ? and ? and");
			params.add(fromDate.concat(start));
			params.add(toDate.concat(end));
		}else if(!StrKit.isBlank(fromDate)&&StrKit.isBlank(toDate)){
			where.append(" user_info.modify_time>=? and");
			params.add(fromDate.concat(start));
		}else if(!StrKit.isBlank(toDate)&&StrKit.isBlank(fromDate)){
			where.append(" user_info.modify_time<=? and");
			params.add(toDate.concat(end));
		}
		if(!StrKit.isBlank(username)){
			where.append(" locate(?,`user`.username)>0 and");
			params.add(username.trim());
		}
		
		String whereStr=where.toString();
		Object[] paramsArray=params.toArray(new Object[params.size()]);
		
		//获取总数据条�?
		String countSql=SqlContext.getSqlByFreeMarker(UserInfo.class, "getCount");
		if(!StrKit.isBlank(whereStr)){
			countSql+=" and"+whereStr.substring(0,whereStr.length()-4);
		}
		
		long count = Db.findFirst(countSql,paramsArray).getLong("count");
		//获取总页码数
		double d=count/(double)page.getPageSize();
		page.setTotalCount(new Double(Math.ceil(d)).intValue());
		
		int limit=(page.getCurrentPage()-1)*page.getPageSize();
//    	//当limit等于0时，去掉子查询中desc，改为升序排序
//		if(limit==0){
//			int n=str2.indexOf("desc");
//			String s1=str2.substring(0, n);
//			String s2=str2.substring(n+"desc".length());
//			str2=s1.concat(s2);
//		}
		sql=str1.concat(whereStr).concat(str2);
		if(logger.isInfoEnabled()){
			logger.info("分页Sql:{}，分页条件:page:{},fromDate:{},toDate:{},username:{}",sql,page.toString(),fromDate,toDate,username);
		}
		params.add(limit);
		params.add(page.getPageSize());
		paramsArray=params.toArray(new Object[params.size()]);
		
		List<Record> list=Db.find(sql,paramsArray);
		List<UserReviewDto> data=new ArrayList<UserReviewDto>(list.size());
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
			dto.setUserId(r.getLong("userid"));
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
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/userReview/history")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"history\\\" href=\\\"javascript:;\\\" title=\\\"审核历史\\\"><i class=\\\"fa fa-history fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		jsFunction.append("return content;}");
		return jsFunction.toString();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public UserReviewDto getUserInfo(long id){
		UserReviewDto userReviewDto=new UserReviewDto();
		Record userInfo = Db.findFirst(SqlContext.getSqlByFreeMarker(UserInfo.class, "findById"), id);
		if(userInfo!=null){
			userReviewDto.setId(userInfo.getLong("id"));
			userReviewDto.setUsername(userInfo.getStr("username"));
			userReviewDto.setCompanyname(userInfo.getStr("companyname"));
			userReviewDto.setContact(userInfo.getStr("contact"));
			userReviewDto.setContactphone(userInfo.getStr("contactphone"));
			userReviewDto.setPublicaccount(userInfo.getStr("publicaccount"));
			userReviewDto.setSafety(userInfo.getStr("safety"));
			userReviewDto.setLicence(userInfo.getStr("licence"));
			userReviewDto.setIdcard(userInfo.getStr("idcard"));
		}
		return userReviewDto;
	}
	
	/**
	 * 验证
	 * @param userReviewDto
	 * @return
	 */
	public String validateReview(UserReviewDto userReviewDto){
		Res res=I18n.use("zh_CN");
		if(userReviewDto.getId()==null||StrKit.isBlank(userReviewDto.getIspass())){
			return res.get("review.parameter.lack");
		}
		if(String.valueOf(ReviewStatus.REJECT.getCode()).equals(userReviewDto.getIspass())&&
				StrKit.isBlank(userReviewDto.getRemark())){
			return res.get("review.parameter.lack");
		}
		if(String.valueOf(ReviewStatus.REJECT.getCode()).equals(userReviewDto.getIspass())&&
				userReviewDto.getRemark().length()>70){
			return res.get("review.remark.length.more.than.70");
		}
		return null;
	}
	
	/**
	 * 审核
	 * @param userReviewDto
	 */
	public boolean review(UserReviewDto userReviewDto,final HttpServletRequest request,long uid){
		final UserInfo userInfo=UserInfo.dao.findById(userReviewDto.getId());
		Date date=new Date();
		userInfo.setChecktime(date);
		userInfo.setCheckuser(uid);
		//
		final UserInfoHistory history=new UserInfoHistory();
		//站内通知消息
		final UserMail userMail=new UserMail();
		userMail.setUserid(userInfo.getUserid());
		userMail.setTime(date);
		userMail.setTitle("资料审核结果通知");
		//审核通过
		if(String.valueOf(ReviewStatus.PASS.getCode()).equals(userReviewDto.getIspass())){
			userInfo.setIspass(ReviewStatus.PASS.getCode());
			userInfo.setAppId(RandomUtil.getUUIDString());
			userInfo.setAppSecret(RandomUtil.GetRandomString(32));
			
			history.setIspass(ReviewStatus.PASS.getCode());
			userMail.setContent("您的资料已经审核通过。");
		}else if(String.valueOf(ReviewStatus.REJECT.getCode()).equals(userReviewDto.getIspass())){
			//审核不通过
			userInfo.setIspass(ReviewStatus.REJECT.getCode());
			userInfo.setRemark(userReviewDto.getRemark());
			
			history.setIspass(ReviewStatus.REJECT.getCode());
			history.setRemark(userReviewDto.getRemark());
			
			userMail.setContent("您的资料审核未通过，未通过原因请查看申请记录。");
		}else{
			//未知操作
			if(logger.isErrorEnabled()){
				logger.error("未知操作");
			}
			throw new IllegalArgumentException("未知操作");
		}
		//审核历史记录
		history.setUserid(userInfo.getUserid());
		history.setCompanyname(userInfo.getCompanyname());
		history.setContact(userInfo.getContact());
		history.setContactphone(userInfo.getContactphone());
		history.setPublicaccount(userInfo.getPublicaccount());
		history.setCheckuser(uid);
		//复制证件图片到备份文件夹
		String licencePath=null;
		String idCardPath=null;
		String safetyPath=null;
		try {
			licencePath = copyImage(userInfo.getLicence(), request);
			idCardPath = copyImage(userInfo.getIdcard(), request);
			safetyPath = copyImage(userInfo.getSafety(), request);
		} catch (IOException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			throw new RuntimeException(e.getMessage(), e);
		}
		history.setLicence(licencePath);
		history.setIdcard(idCardPath);
		history.setSafety(safetyPath);
		history.setChecktime(date);
		
		return Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				try{
					history.save();
					userMail.save();
					userInfo.update();
					String username=User.dao.findById(userInfo.getUserid()).getUsername();
					logService.addLog(request, "审核用户id为"+userInfo.getUserid()+"用户名为"+username+"的用户资料，审核结果�?"+
							(userInfo.getIspass()==ReviewStatus.PASS.getCode()?"通过审核":"驳回"),
							OperaResult.SUCCESS, null);
					return true;
				}catch(Exception e){
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
					return false;
				}
			}
		});
	}
	
	/**
	 * 复制证件照片
	 * @param path
	 * @throws IOException 
	 * @return 
	 */
	private String copyImage(String path,HttpServletRequest request) throws IOException{
		String dirName="uploadimages";
		//获取文件后缀即文件类�?
		String extension=path.substring(path.lastIndexOf("."));
		path=WebUtil.getRootPath(request)+File.separator+dirName+File.separator+path;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
		Date date=new Date();
		
		String basePath=FileUtil.getAbsolutePath("userImages");
		StringBuilder sb=new StringBuilder(basePath);
		sb.append(File.separator);
		String dir=sdf.format(date);
		sb.append(dir);
		sb.append(File.separator);
		String uuid=RandomUtil.getUUIDString();
		sb.append(uuid);
		sb.append(extension);
		String toPath=sb.toString();
		FileUtil.copyFile(new File(path), new File(toPath));
		return dir+File.separator+uuid+extension;
	}
	
	
	/**
	 * 获取客户审核历史记录信息
	 * @param id 用户id
	 * @return
	 */
	public List<UserReviewDto> getHistoryList(long id){
		List<Record> rlist=Db.find(SqlContext.getSqlByFreeMarker(UserInfoHistory.class, "getList"),id);
		List<UserReviewDto> resultList=new ArrayList<UserReviewDto>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Record r:rlist){
			UserReviewDto usDto=new UserReviewDto();
			usDto.setId(r.getLong("id"));
			usDto.setUsername(r.getStr("username"));
			usDto.setCompanyname(r.getStr("companyname"));
			usDto.setContact(r.getStr("contact"));
			usDto.setContactphone(r.getStr("contactphone"));
			usDto.setChecktime(sdf.format(r.getDate("checktime")));
			usDto.setIspass(r.getInt("ispass")==ReviewStatus.PASS.getCode()?"通过":"驳回");
			usDto.setCheckuser(r.getStr("checkuser"));
			usDto.setRemark(r.getStr("remark"));
			resultList.add(usDto);
		}
		return resultList;
	}
	
	/**
	 * 获取审核历史记录详情
	 * @param id
	 * @return
	 */
	public UserReviewDto getUserInfoHistoryDetail(long id){
		Record r=Db.findFirst(SqlContext.getSqlByFreeMarker(UserInfoHistory.class, "findById"),id);
		UserReviewDto userReviewDto=new UserReviewDto();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(r!=null){
			userReviewDto.setId(r.getLong("id"));
			userReviewDto.setUsername(r.getStr("username"));
			userReviewDto.setCompanyname(r.getStr("companyname"));
			userReviewDto.setContact(r.getStr("contact"));
			userReviewDto.setContactphone(r.getStr("contactphone"));
			userReviewDto.setLicence(r.getStr("licence"));
			userReviewDto.setIdcard(r.getStr("idcard"));
			userReviewDto.setSafety(r.getStr("safety"));
			userReviewDto.setChecktime(sdf.format(r.getDate("checktime")));
			userReviewDto.setIspass(r.getInt("ispass")==ReviewStatus.PASS.getCode()?"通过":"驳回");
			userReviewDto.setCheckuser(r.getStr("checkuser"));
			userReviewDto.setRemark(r.getStr("remark"));
		}
		return userReviewDto;
	}
	
	
	public File getHistoryImage(long id,String type){
		if(!"licence".equals(type)&&!"idcard".equals(type)&&!"safety".equals(type)){
			return null;
		}
		String sql= SqlContext.getSqlByFreeMarker(UserInfoHistory.class, "getImg");
		sql=sql.replace("*", type);
		String path=UserInfoHistory.dao.findFirst(sql,id).getStr(type);
		if(path!=null){
			path=FileUtil.getAbsolutePath("uploadimages")+File.separator+path;
			return new File(path);
		}
		return null;
	}
	
	public static void main(String[] args) {
//		String uuid = UUID.randomUUID().toString();
//		uuid=uuid.replace("-", "");
//		System.out.println(uuid+",length:"+uuid.length());
//		String toPath=Thread.currentThread().getContextClassLoader().getResource("userImages").getPath();
//		System.out.println(toPath);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		String basePath=Thread.currentThread().getContextClassLoader().getResource("userImages").getPath();
		StringBuilder sb=new StringBuilder(basePath);
		sb.append(File.separator);
		sb.append(sdf.format(new Date()));
		sb.append(File.separator);
		sb.append(RandomUtil.getUUIDString());
		sb.append(".jpg");
		String toPath=sb.toString();
		System.out.println(toPath);
	}
	
}
