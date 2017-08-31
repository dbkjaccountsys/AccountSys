package com.dbkj.account.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.dbkj.account.util.RandomUtil;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;

/**
 * 发送短信相关操作
 * @author DELL
 *
 */
public class SmsService {
	
	private static final Logger logger=LoggerFactory.getLogger(SmsService.class);
	
	private static final String YES="2000";
	private static final String NO="2001";
	private static final String SMS_SERVICE_URL="messageService";
	
	/**
	 * 发送短信
	 * @param mobile 手机号码
	 * @param content 短信内容
	 * @param isLongMessage 是否为长短信，2000：是，2001：否
	 * @return
	 */
	public Result sendSms(String mobile,String content,String isLongMessage){
		Map<String, Object> paraMap=new HashMap<String, Object>(7);
		paraMap.put("reqId", RandomUtil.getSeqId());
		paraMap.put("servId", PropKit.get("servId"));
		paraMap.put("servAuth", PropKit.get("servAuth"));
		paraMap.put("mobile", mobile);
		paraMap.put("smsContent", content);
		paraMap.put("longSms", isLongMessage);
		paraMap.put("action", 10000);
		String jsonStr=JSON.toJSONString(paraMap);
		if(logger.isInfoEnabled()){
			logger.info("发送短信:{}",jsonStr);
		}
		
		String result = HttpKit.post(PropKit.get(SmsService.SMS_SERVICE_URL), jsonStr);
		if(logger.isInfoEnabled()){
			logger.info("发送短信结果:{}",result);
		}
		return JSON.parseObject(result, Result.class);
	}
	
	/**
	 * 发送短信
	 * @param mobile 手机号码
	 * @param content 短信内容
	 * @return
	 */
	public Result sendSms(String mobile,String content){
		return sendSms(mobile, content, SmsService.NO);
	}

	
	/**
	 * 封装发送短信操作结果的类
	 * @author DELL
	 *
	 */
	public static class Result{
		private String reqId;
		private String action;
		private String status;
		private String content;
		public String getReqId() {
			return reqId;
		}
		public void setReqId(String reqId) {
			this.reqId = reqId;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
		
	}
	
	public static void main(String[] args) {
		PropKit.use("config.properties");
		new SmsService().sendSms("13277054876", "测试短信");
	}
}
