package com.dbkj.account.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dic.ReviewStatus;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.VoiceReviewDto;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.User;
import com.dbkj.account.model.UserMail;
import com.dbkj.account.model.UserVoice;
import com.dbkj.account.model.UserVoiceAudit;
import com.dbkj.account.util.FileUtil;
import com.dbkj.account.util.IdGen;
import com.dbkj.account.util.SqlUtil;
import com.dbkj.account.util.WebUtil;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class VoiceReviewService {
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	private AuthorityService authorityService;
	private LogService logService;

	/**
	 * 语音审核的分页数据
	 * @param page
	 * @param fromDate
	 * @param toDate
	 * @param username
	 * @param roleId
	 */
	public void getPage(Page<VoiceReviewDto> page,String fromDate,String toDate,String username,long roleId){
		String sql=SqlUtil.getSql(UserVoice.class, "getList").toLowerCase();
		int index=sql.lastIndexOf("where")+"where".length();
		String str1=sql.substring(0,index);
		String str2=sql.substring(index);
		
		List<Object> params=new ArrayList<Object>();
		StringBuilder where=new StringBuilder(80);
		
		String start=" 00:00:00";
		String end=" 23:59:59";
		if(!StrKit.isBlank(fromDate)&&!StrKit.isBlank(toDate)){
			where.append(" user_voice.uploadtime between ? and ? and");
			params.add(fromDate.concat(start));
			params.add(toDate.concat(end));
		}else if(!StrKit.isBlank(fromDate)&&StrKit.isBlank(toDate)){
			where.append(" user_voice.uploadtime>=? and");
			params.add(fromDate.concat(start));
		}else if(!StrKit.isBlank(toDate)&&StrKit.isBlank(fromDate)){
			where.append(" user_voice.uploadtime<=? and");
			params.add(toDate.concat(end));
		}
		if(!StrKit.isBlank(username)){
			where.append(" locate(?,`user`.username)>0 and");
			params.add(username.trim());
		}
		
		String whereStr=where.toString();
		Object[] paramsArray=params.toArray(new Object[params.size()]);
		
		//获取总数据条数
		String countSql=SqlUtil.getSql(UserVoice.class, "getCount");
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
		List<VoiceReviewDto> data=new ArrayList<VoiceReviewDto>();
		
		String opera=getOpera(roleId);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Record r:list){
			VoiceReviewDto dto=new VoiceReviewDto();
			dto.setId(r.getLong("id"));
			dto.setUsername(r.getStr("username"));
			dto.setUpdateTime(sdf.format(r.getDate("uploadtime")));
			dto.setVoiceName(r.getStr("voicename"));
			dto.setContent(r.getStr("content"));
			dto.setOpera(opera);
			data.add(dto);
		}
		page.setData(data);
	}
	
	public void getPage(Page<VoiceReviewDto> page,long roleId){
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
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/voiceReview/voiceInfo")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"review\\\" href=\\\"javascript:;\\\" title=\\\"审核\\\"><i class=\\\"fa fa-edit fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		//判断用户是否查看审核历史的权限
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/voiceReview/history")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"history\\\" href=\\\"javascript:;\\\" title=\\\"审核历史\\\"><i class=\\\"fa fa-history fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		jsFunction.append("return content;}");
		return jsFunction.toString();
	}
	
	public VoiceReviewDto getVoiceInfo(long id){
		VoiceReviewDto vDto=new VoiceReviewDto();
		UserVoice userVoice=UserVoice.dao.findFirst(SqlUtil.getSql(UserVoice.class, "findById"),id);
		System.out.println(userVoice.toString());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(userVoice!=null){
//			User user=User.dao.findById(userVoice.getUserid());
//			System.out.println(user.toString());
			vDto.setId(userVoice.getId());
			vDto.setUsername(userVoice.getStr("username"));
			vDto.setUpdateTime(sdf.format(userVoice.getUploadtime()));
			vDto.setVoiceName(userVoice.getVoicename());
			vDto.setContent(userVoice.getContent());
			vDto.setFilePath(userVoice.getFilepath());
		}
		return vDto;
	}
	
	public String validateReview(VoiceReviewDto voiceReviewDto){
		Res res=I18n.use("zh_CN");
		if(voiceReviewDto.getId()==null||StrKit.isBlank(voiceReviewDto.getStatus())){
			return res.get("review.parameter.lack");
		}
		if(String.valueOf(ReviewStatus.REJECT.getCode()).equals(voiceReviewDto.getStatus())&&
				StrKit.isBlank(voiceReviewDto.getReason())){
			return res.get("review.parameter.lack");
		}
		if(String.valueOf(ReviewStatus.REJECT.getCode()).equals(voiceReviewDto.getStatus())&&
				voiceReviewDto.getReason().length()>70){
			return res.get("review.remark.length.more.than.70");
		}
		return null;
	}
	
	/**
	 * 审核
	 * @param userReviewDto
	 * @param request
	 * @param uid
	 */
	public boolean review(VoiceReviewDto voiceReviewDto,final HttpServletRequest request,long uid){
		final UserVoice userVoice=UserVoice.dao.findById(voiceReviewDto.getId());
		Date date=new Date();
		//审核历史记录
		final UserVoiceAudit userVoiceAudit=new UserVoiceAudit();
		//站内消息
		final UserMail userMail=new UserMail();
		userMail.setUserid(userVoice.getUserid());
		userMail.setTime(date);
		userMail.setTitle("语音审核结果通知");
		//审核通过
		if(String.valueOf(ReviewStatus.PASS.getCode()).equals(voiceReviewDto.getStatus())){
			userVoice.setStatus(ReviewStatus.PASS.getCode());
			userVoiceAudit.setStatus(ReviewStatus.PASS.getCode());
			userMail.setContent("您的语音已经审核通过。");
		}else if(String.valueOf(ReviewStatus.REJECT.getCode()).equals(voiceReviewDto.getStatus())){
			userVoice.setStatus(ReviewStatus.REJECT.getCode());
			userVoice.setReason(voiceReviewDto.getReason());
			
			userVoiceAudit.setStatus(ReviewStatus.REJECT.getCode());
			userVoiceAudit.setReason(voiceReviewDto.getReason());
			
			userMail.setContent("您的资料审核未通过，未通过原因请查看申请记录。");
		}else{
			//未知操作
			if(logger.isErrorEnabled()){
				logger.error("未知操作");
			}
			throw new IllegalArgumentException("未知操作");
		}
		userVoiceAudit.setUserid(userVoice.getUserid());
		userVoiceAudit.setUpdatetime(userVoice.getUploadtime());
		userVoiceAudit.setVoicename(userVoice.getVoicename());
		userVoiceAudit.setContent(userVoice.getContent());
		userVoiceAudit.setChecktime(date);
		userVoiceAudit.setCheckuser(uid);
		//复制语音文件到指定路径
		String voicePath=null;
		try {
			voicePath=copyVoice(userVoice.getFilepath(), request);
		} catch (IOException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			throw new RuntimeException(e.getMessage(), e);
		}
		userVoiceAudit.setFilename(voicePath);
		
		if(logService==null){
			logService=new LogService();
		}
		
		return Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				try{
					userMail.save();
					userVoice.update();
					userVoiceAudit.setVId(userVoice.getId());
					userVoiceAudit.save();
					String username=User.dao.findById(userVoice.getUserid()).getUsername();
					logService.addLog(request, "审核用户id为"+userVoice.getUserid()+"的用户名"+username+"用户资料，审核结果为"+
							(userVoice.getStatus()==ReviewStatus.PASS.getCode()?"通过审核":"驳回"),
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
	 * 复制语音文件
	 * @param path
	 * @throws IOException 
	 * @return 
	 */
	private String copyVoice(String path,HttpServletRequest request) throws IOException{
		//获取文件后缀即文件类型
		String extension=path.substring(path.lastIndexOf("."));
		path=WebUtil.getRootPath(request)+File.separator+"uploadvoices"+File.separator+path;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
		Date date=new Date();
		
		String dirName="voices";
		String basePath=FileUtil.getAbsolutePath(dirName);
		StringBuilder sb=new StringBuilder(basePath);
		sb.append(File.separator);
		String dir=sdf.format(date);
		sb.append(dir);
		sb.append(File.separator);
		String uuid=IdGen.randomString();
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
	public List<VoiceReviewDto> getHistoryList(long id){
		List<Record> rlist=Db.find(SqlUtil.getSql(UserVoiceAudit.class, "getList"),id);
		List<VoiceReviewDto> resultList=new ArrayList<VoiceReviewDto>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Record r:rlist){
			VoiceReviewDto dto=new VoiceReviewDto();
			dto.setId(r.getLong("id"));
//			dto.setVid(r.getLong("v_id"));
			dto.setUsername(r.getStr("username"));
			dto.setUpdateTime(sdf.format(r.getDate("updatetime")));
			dto.setVoiceName(r.getStr("voicename"));
			dto.setContent(r.getStr("content"));
			dto.setCheckUser(r.getStr("checkuser"));
			dto.setStatus(r.getInt("status")==ReviewStatus.PASS.getCode()?"通过审核":"驳回");
			resultList.add(dto);
		}
		return resultList;
	}
	
	/**
	 * 获取历史详情
	 * @param id
	 * @return
	 */
	public VoiceReviewDto getHistoryDetail(long id){
		VoiceReviewDto vDto=new VoiceReviewDto();
		UserVoiceAudit uvd = UserVoiceAudit.dao.findById(id);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(uvd!=null){
			vDto.setId(uvd.getId());
			vDto.setVid(uvd.getVId());
			vDto.setCheckTime(sdf.format(uvd.getChecktime()));
			vDto.setFileName(uvd.getFilename());
			vDto.setVoiceName(uvd.getVoicename());
			vDto.setContent(uvd.getContent());
			vDto.setStatus(uvd.getStatus()==ReviewStatus.PASS.getCode()?"通过审核":"驳回");
			vDto.setReason(uvd.getReason());
			String username=User.dao.findById(uvd.getUserid()).getUsername();
			vDto.setUsername(username);
			String checkUser=Admin.dao.findById(uvd.getCheckuser()).getUsername();
			vDto.setCheckUser(checkUser);
		}
		return vDto;
	}
	
	public File getHistoryVoice(String name){
		if(!StrKit.isBlank(name)){
			String basePath=FileUtil.getAbsolutePath("voices");
			return new File(basePath,name);
		}
		return null;
	}
}
