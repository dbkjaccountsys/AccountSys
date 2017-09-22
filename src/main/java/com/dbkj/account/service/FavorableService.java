package com.dbkj.account.service;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.config.SqlContext;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dto.FavorableDto;
import com.dbkj.account.dto.FavorableStatus;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.Result;
import com.dbkj.account.model.Favorable;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FavorableService {
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	private AuthorityService authorityService;
	private LogService logService;
	
	public void getPage(Page<FavorableDto> page,long roleId){
		long count = Favorable.dao.findFirst(SqlContext.getSqlByFreeMarker(Favorable.class, "getCount")).getLong("count");
		page.setRecords(count);
		//获取总页码数
	    double d=count/(double)page.getPageSize();
	    page.setTotalCount(new Double(Math.ceil(d)).intValue());
		int limit=(page.getCurrentPage()-1)*page.getPageSize();
		List<Favorable> flist=Favorable.dao.find(SqlContext.getSqlByFreeMarker(Favorable.class, "getPage"),limit,page.getPageSize());
		List<FavorableDto> fdlist=new ArrayList<FavorableDto>(page.getPageSize());
		
		String opera=getOpera(roleId);
		for(Favorable f:flist){
			FavorableDto dto=convertToFavorableDto(f,opera);
			fdlist.add(dto);
		}
		page.setData(fdlist);
	}
	
	private FavorableDto convertToFavorableDto(Favorable favorable,String opera){
		FavorableDto dto=new FavorableDto();
		if(favorable!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dto.setId(favorable.getId());
			dto.setName(favorable.getName());
			dto.setBeginTime(sdf.format(favorable.getBegintime()));
			dto.setEndTime(sdf.format(favorable.getEndtime()));
			dto.setRate(favorable.getRate().floatValue());
			dto.setMincharge(favorable.getMincharge().doubleValue());
			dto.setMaxcharge(favorable.getMaxcharge().doubleValue());
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
		 *		content+="&nbsp;&nbsp;<a class=\"cancel\" href=\"javascript:;\" title=\"取消活动\"><i class=\"fa fa-times fa-2\"></i></a>&nbsp;&nbsp;";
		 *		return content;
		 * }
		 */
		StringBuilder jsFunction=new StringBuilder("function(cellvalue, options, rowObject){");
		jsFunction.append("var content=\"\";");
		//判断用户是否有审核用户的权限
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/favorable/edit")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"edit\\\" href=\\\"javascript:;\\\" title=\\\"编辑\\\"><i class=\\\"fa fa-edit fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		//判断用户是否查看审核历史的权限
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/favorable/cancel")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"cancel\\\" href=\\\"javascript:;\\\" title=\\\"取消活动\\\"><i class=\\\"fa fa-times fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		jsFunction.append("return content;}");
		return jsFunction.toString();
	}
	
	public FavorableDto getFavorable(long id){
		Favorable favorable=Favorable.dao.findById(id);
		return convertToFavorableDto(favorable, null);
	}
	
	public boolean addFavorable(FavorableDto dto) throws ParseException{
		Favorable favorable=new Favorable();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		favorable.setName(dto.getName());
		favorable.setBegintime(simpleDateFormat.parse(dto.getBeginTime()));
		favorable.setEndtime(simpleDateFormat.parse(dto.getEndTime()));
		favorable.setRate(new BigDecimal(dto.getRate()));
		favorable.setMincharge(new BigDecimal(dto.getMincharge()));
		favorable.setMaxcharge(new BigDecimal(dto.getMaxcharge()));
		favorable.setStatus(1);
		return favorable.save();
	}
	
	/**
	 * 添加优惠活动
	 * @param dto
	 * @param request
	 * @return
	 */
	public boolean addFavorable(FavorableDto dto,HttpServletRequest request){
		boolean result=false;
		if(logService==null){
			logService=new LogService();
		}
		try {
			result = addFavorable(dto);
			logService.addLog(request, "添加优惠活动，添加数据内容："+JSON.toJSONString(dto), 
					result?OperaResult.SUCCESS:OperaResult.FAIL, null);
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
			logService.addLog(request, "添加优惠活动，添加数据内容："+JSON.toJSONString(dto), 
					OperaResult.EXCEPTION, e.getMessage());
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	public boolean updateFavorable(final FavorableDto dto,final HttpServletRequest request){
		if(logService==null){
			logService=new LogService();
		}
		return Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				Favorable favorable=Favorable.dao.findById(dto.getId());
				String before=favorable.toJson();
				try {
					SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
					favorable.setName(dto.getName());
					favorable.setBegintime(simpleDateFormat.parse(dto.getBeginTime()));
					favorable.setEndtime(simpleDateFormat.parse(dto.getEndTime()));
					favorable.setRate(new BigDecimal(dto.getRate()));
					favorable.setMincharge(new BigDecimal(dto.getMincharge()));
					favorable.setMaxcharge(new BigDecimal(dto.getMaxcharge()));
					boolean result=favorable.update();
					logService.addLog(request, "修改优惠活动，修改前："+before+"，修改后："+favorable.toJson(),
							result?OperaResult.SUCCESS:OperaResult.FAIL, null);
					return result;
				} catch (ParseException e) {
					logger.error(e.getMessage(),e);
					logService.addLog(request, "修改优惠活动，修改前："+before+"，修改后："+favorable.toJson(), 
							OperaResult.EXCEPTION, e.getMessage());
					return false;
				}
			}
		});
		
	}
	
	/**
	 * 取消活动
	 * @param id
	 * @return
	 */
	public Result<?> cancelFavorable(long id,HttpServletRequest request){
		Favorable favorable=Favorable.dao.findById(id);
		favorable.setStatus(FavorableStatus.DISABLE.getValue());
		boolean b = favorable.update();
		if(logService==null){
			logService=new LogService();
		}
		logService.addLog(request, "取消优惠活动，取消活动："+favorable.toJson(),
				b?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return new Result(b);
	}

}
