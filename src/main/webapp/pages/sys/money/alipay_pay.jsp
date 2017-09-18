<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.dbkj.account.sys.Config"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>付款</title>
</head>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.alipay.api.*"%>
<%@ page import="com.alipay.api.request.*"%>
<%
	AlipayClient alipayClient = new DefaultAlipayClient(Config.gatewayUrl,Config.appid,Config.privatekey,"json","UTF-8",Config.publickey,Config.signtype);
	AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
	alipayRequest.setReturnUrl(Config.returnUrl);
	alipayRequest.setNotifyUrl(Config.notifyUrl);
	
	String out_trade_no = request.getAttribute("tradeno").toString();
	String total_amount = request.getAttribute("amount").toString();
	String subject = request.getAttribute("subject").toString();
	
	//String body = "商品描述";
	
	alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
			+ "\"total_amount\":\""+ total_amount +"\","
			+ "\"subject\":\""+ subject +"\","
			//+ "\"body\":\""+ body +"\","
			+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
	
	String result = alipayClient.pageExecute(alipayRequest).getBody();
	
	out.println(result);
%>
<body>
</body>
</html>