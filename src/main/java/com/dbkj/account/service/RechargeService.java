package com.dbkj.account.service;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.config.SqlContext;
import com.dbkj.account.dic.ChargeType;
import com.dbkj.account.dic.Constant;
import com.dbkj.account.dic.OperaResult;
import com.dbkj.account.dto.CompanyDto;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.RechargeDto;
import com.dbkj.account.dto.RechargeHistoryDto;
import com.dbkj.account.model.Admin;
import com.dbkj.account.model.User;
import com.dbkj.account.model.UserInfo;
import com.dbkj.account.model.UserRecharge;
import com.dbkj.account.util.ValidateUtil;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RechargeService {
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	private AuthorityService authorityService;
	private LogService logService;

	/**
	 * 获取所有可用公司信息
	 * @return
	 */
	public List<CompanyDto> getCompanyList(){
		List<UserInfo> ulist = UserInfo.dao.find(SqlContext.getSqlByFreeMarker(UserInfo.class, "getCompanys"));
		List<CompanyDto> clist=new ArrayList<CompanyDto>(ulist.size()+1);
		CompanyDto cd=new CompanyDto(0L,"全部");
		clist.add(cd);
		for(UserInfo info:ulist){
			CompanyDto dto=new CompanyDto(info.getUserid(),info.getCompanyname());
			clist.add(dto);
		}
		return clist;
	}
	
	public void getList(Page<RechargeDto> page,Long uid,long roleId){
		List<Object> params=new ArrayList<Object>(3);
		Map<String,Object> paraMap=new HashMap<>(1);
		if(uid!=null&&uid!=0){
			paraMap.put("userId",uid);
			params.add(uid);
		}
		String sql= SqlContext.getSqlByFreeMarker(User.class,"getRechargeList",paraMap);
		String countSql=SqlContext.getSqlByFreeMarker(User.class,"getRechargeCount",paraMap);
		
		//获取数据总行数
		long count=Db.findFirst(countSql,params.toArray(new Object[params.size()])).getLong("count");
		page.setRecords(count);
		//获取总页数
		page.setTotalCount((int)Math.ceil(count/(double)page.getPageSize()));
		
		int limit=(page.getCurrentPage()-1)*page.getPageSize();
		params.add(limit);
		params.add(page.getPageSize());
		List<User> list=User.dao.find(sql,params.toArray(new Object[params.size()]));
		List<RechargeDto> rlist=new ArrayList<RechargeDto>(list.size());
		String opera=getOpera(roleId);
		for(User info:list){
			RechargeDto dto=convert2RechargeDto(info, opera);
			rlist.add(dto);
		}
		page.setData(rlist);
	}
	
	private RechargeDto convert2RechargeDto(Object obj,String opera){
		RechargeDto dto=new RechargeDto();
		if(obj instanceof User){
			User info=(User)obj;
			dto.setId(info.getId());
			dto.setUsername(info.getUsername());
			dto.setCompanyName(info.getStr("companyname"));
			BigDecimal charge=info.getCharge();
			if(charge!=null){
				dto.setCharge(charge.doubleValue());
			}
			dto.setOpera(opera);
		}
		return dto;
	}
	
	private String getOpera(long roleId){
		authorityService=new AuthorityService();
		/**
		 * function(cellvalue, options, rowObject){
		 *		var content="";
		 *		content+="&nbsp;&nbsp;<a class=\"review\" href=\"javascript:;\" title=\"充值\"><i class=\"fa fa-cc-visa fa-2\"></i></a>&nbsp;&nbsp;";
		 *		return content;
		 * }
		 */
		StringBuilder jsFunction=new StringBuilder("function(cellvalue, options, rowObject){");
		jsFunction.append("var content=\"\";");
		//判断用户是否有充值的权限
		if(authorityService.isPermissionOfAdmin(roleId, "/manage/recharge/charging")){
			jsFunction.append("content+=\"&nbsp;&nbsp;<a class=\\\"recharge\\\" href=\\\"javascript:;\\\" title=\\\"充值\\\"><i class=\\\"fa fa-cc-visa fa-2\\\"></i></a>&nbsp;&nbsp;\";");
		}
		jsFunction.append("return content;}");
		return jsFunction.toString();
	}
	
	public RechargeDto getRechargeInfo(Long uid){
		User user=User.dao.findFirst(SqlContext.getSqlByFreeMarker(User.class, "getRechargeInfo"),uid);
		return convert2RechargeDto(user, null);
	}
	
	public String validateCharge(RechargeDto dto){
		Res res=I18n.use("zh_CN");
		if(dto==null||dto.getId()==null||dto.getChargeAmount()==null||dto.getActualAmount()==null){
			return res.get("recharge.parameter.lack");
		}
		if(ValidateUtil.isDigital(dto.getChargeAmount())||ValidateUtil.isDigital(dto.getActualAmount())){
			return res.get("recharge.amount.format.incorrect");
		}
		return null;
	}
	
	/**
	 * 充值
	 * @param dto
	 * @param request
	 * @return
	 */
	public boolean charge(RechargeDto dto,final HttpServletRequest request){
		//更新余额
		final User user=User.dao.findById(dto.getId());
		BigDecimal charge=user.getCharge();
		charge=charge.add(new BigDecimal(dto.getChargeAmount()));
		user.setCharge(charge);
		
		Date date=new Date();
		Admin admin=(Admin) request.getSession().getAttribute(Constant.CURRENT_USER);
		
		//添加充值记录
		final UserRecharge ur=new UserRecharge();
		ur.setUserid(user.getId());
		ur.setChargetype(ChargeType.BACKSTAGE.getValue());
		ur.setTime(date);
		ur.setCharge(new BigDecimal(dto.getChargeAmount()));
		ur.setRealcharge(new BigDecimal(dto.getActualAmount()));
		ur.setChargeuser(admin.getId());
		
		boolean result = Db.tx(new IAtom() {
			
			public boolean run() throws SQLException {
				boolean result=user.update();
				if(result){
					return ur.save();
				}
				return result;
			}
		});
		//添加操作日志
		if(logService==null){
			logService=new LogService();
		}
		String companyName=UserInfo.dao.findFirst(SqlContext.getSqlByFreeMarker(UserInfo.class,
				"getCompanyNameByUserId"),user.getId()).getCompanyname();
		StringBuilder content=new StringBuilder("给用户id：")
				.append(user.getId())
				.append(",用户名为："+user.getUsername())
				.append(",公司名称："+companyName)
				.append(",充值："+ur.getCharge().doubleValue())
				.append("实际到账："+ur.getRealcharge().doubleValue());
		logService.addLog(request, content.toString(), result?OperaResult.SUCCESS:OperaResult.FAIL, null);
		return result;
	}
	
	public void getHistoryList(Page<RechargeHistoryDto> page,String startTime,String endTime,String companyName){
		List<Object> params=new ArrayList<Object>();
		Map<String,Object> paraMap=new HashMap<>();
		if(!StrKit.isBlank(startTime)){
			paraMap.put("startTime",startTime);
			params.add(startTime);
		}
		if(StrKit.notBlank(endTime)){
			paraMap.put("endTime",endTime);
			params.add(endTime);
		}
		
		if(!StrKit.isBlank(companyName)){
			List<UserInfo> list = UserInfo.dao.find(SqlContext.getSqlByFreeMarker(UserInfo.class, "findByCompanyName"),companyName+"%");
			paraMap.put("userList",list);
		}
		String sql=SqlContext.getSqlByFreeMarker(UserRecharge.class,"getPage",paraMap);
		String countSql=SqlContext.getSqlByFreeMarker(UserRecharge.class,"getCount",paraMap);
		
		long count=Db.queryLong(countSql, params.toArray(new Object[params.size()]));
		page.setRecords(count);
		page.setTotalCount((int)Math.ceil(count/(double)page.getPageSize()));
		
		int limit = (page.getCurrentPage()-1)*page.getPageSize();
		params.add(limit);
		params.add(page.getPageSize());

		if(logger.isInfoEnabled()){
			logger.info("分页SQL：{}，查询参数：{}",sql,JSON.toJSON(params));
		}
		
		List<UserRecharge> list=UserRecharge.dao.find(sql,params.toArray(new Object[params.size()]));
		List<RechargeHistoryDto> rows=new ArrayList<RechargeHistoryDto>(list.size());
		for(UserRecharge recharge:list){
			RechargeHistoryDto dto=convert2RechargeHistoryDto(recharge);
			rows.add(dto);
		}
		page.setData(rows);
	}
	
	private RechargeHistoryDto convert2RechargeHistoryDto(UserRecharge recharge){
		RechargeHistoryDto dto=new RechargeHistoryDto();
		if(recharge!=null){
			dto.setId(recharge.getId());
			dto.setUserId(recharge.getUserid());
			dto.setUsername(recharge.getStr("username"));
			dto.setCompanyName(recharge.getStr("companyname"));
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dto.setTime(sdf.format(recharge.getTime()));
			dto.setCharge(recharge.getCharge().doubleValue());
			dto.setRealcharge(recharge.getRealcharge().doubleValue());
			ChargeType chargeType=ChargeType.valueOf(recharge.getChargetype());
			if(chargeType!=null){
				dto.setChargeType(chargeType.getDesc());
			}
			dto.setSerialNum(recharge.getSerialnum());
			dto.setChargeUser(recharge.getStr("chargeuser"));
		}
		return dto;
	}
	
	public static void main(String[] args) {
//		String sql=SqlContext.getSql(UserInfo.class, "getRechargeList").toLowerCase();
//		int index=sql.indexOf("where")+"where".length();
//		String str1=sql.substring(0, index);
//		String str2=sql.substring(index);
//		System.out.println("str1:"+str1+"\nstr2:"+str2);
	}
}
