package com.dbkj.account.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.dto.FavorableDto;
import com.dbkj.account.util.ValidateUtil;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

public class FavorableValidator extends Validator{
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());

	@Override
	protected void validate(Controller c) {
		String actionKey=getActionKey();
		FavorableDto f=c.getBean(FavorableDto.class,"f");
		Res res=I18n.use("zh_CN");
		if("/manage/favorable/update".equals(actionKey)&&f.getId()==null){
			addError("idMsg", res.get("favorable.id.empty"));
		}
		String name=f.getName();
		if(StrKit.isBlank(name)){
			addError("nameMsg", res.get("favorable.name.empty"));
		}else if(name.length()>15){
			addError("nameMsg", res.get("favorable.name.length.more.than.15"));
		}
		
		String beginTime=f.getBeginTime();
		String dateFormat="yyyy-MM-dd HH:mm:ss";
		boolean flag=true;//标志位，判断开始日期是否是正确格式的日期字符串
		if(StrKit.isBlank(beginTime)){
			addError("begintimeMsg", res.get("favorable.begintime.empty"));
			flag=false;
		}else if(!ValidateUtil.validateDateFormat(beginTime, dateFormat)){
			addError("begintimeMsg", res.get("favorable.begintime.format.incorrect"));
			flag=false;
		}
		
		String endTime=f.getEndTime();
		if(StrKit.isBlank(endTime)){
			addError("endtimeMsg", res.get("favorable.endtime.empty"));
		}else if(!ValidateUtil.validateDateFormat(endTime, dateFormat)){
			addError("endtimeMsg", res.get("favorable.endtime.format.incorrect"));
		}else if(flag){
			SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
			try {
				//判断结束时间是否在开始时间之后
				if(sdf.parse(beginTime).getTime()>sdf.parse(endTime).getTime()){
					addError("endtimeMsg", res.get("favorable.endtime.not.before.begintime"));
				}
			} catch (ParseException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		Float rate=f.getRate();
		if(rate==null){
			addError("rateMsg", res.get("favorable.rate.empty"));
		}else if(rate.floatValue()<1f||rate.floatValue()>999.99f){
			addError("rateMsg", res.get("favorable.rate.formate.incorrect"));
		}
		
		Double mincharge=f.getMincharge();
		flag=true;
		if(mincharge==null){
			addError("minchargeMsg", res.get("favorable.mincharge.empty"));
			flag=false;
		}else if(mincharge.doubleValue()<0){
			addError("minchargeMsg", res.get("favorable.mincharge.not.more.than.0"));
			flag=false;
		}
		
		Double maxcharge=f.getMaxcharge();
		if(null==maxcharge){
			addError("maxchargeMsg", res.get("favorable.maxcharge.empty"));
		}else if(maxcharge.doubleValue()<0d){
			addError("maxchargeMsg", res.get("favorable.maxcharge.not.more.than.0"));
		}else if(flag&&maxcharge.doubleValue()<mincharge.doubleValue()){
			addError("maxchargeMsg", res.get("favorable.maxcharge.not.more.than.mincharge"));
		}
	}

	@Override
	protected void handleError(Controller c) {
		String actionKey=getActionKey();
		c.keepBean(FavorableDto.class,"f");
		if("/manage/favorable/save".equals(actionKey)){
			c.render("add.html");
		}else{
			c.render("edit.html");
		}
	}

}
