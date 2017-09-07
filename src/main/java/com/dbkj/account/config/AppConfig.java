package com.dbkj.account.config;

import com.dbkj.account.controller.ForgetPasswordController;
import com.dbkj.account.controller.VertifyCodeController;
import com.dbkj.account.interceptor.AdminAuthorityTemplateDirectiveInterceptor;
import com.dbkj.account.interceptor.AuthInterceptor;
import com.dbkj.account.interceptor.LoginInterceptor;
import com.dbkj.account.interceptor.UserAuthorityTemplateDirectiveInterceptor;
import com.dbkj.account.model._MappingKit;
import com.dbkj.account.service.OperaTypeService;
import com.dbkj.account.sys.SysRoutes;
import com.dbkj.account.sys.UserPhoneThread;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;

public class AppConfig extends JFinalConfig{

	@Override
	public void configConstant(Constants me) {
		PropKit.use("config.properties");
	    //设置当前是否为开发模式
	    me.setDevMode(PropKit.getBoolean("devMode"));
	    //设置上传最大限制尺寸
	    me.setMaxPostSize(1024*1024*10);
	    me.setViewType(ViewType.FREE_MARKER);
	    me.setErrorView(401, "/pages/other/401.html");
	    me.setErrorView(404, "/pages/other/404.html");
	    me.setErrorView(500, "/pages/other/500.html");
	}

	@Override
	public void configRoute(Routes me) {
		me.add(new SysRoutes());
		
		me.add("/vertifyCode",VertifyCodeController.class);
		me.add("/forget",ForgetPasswordController.class);
		me.add(new ManageRoute());
		
		UserPhoneThread th = new UserPhoneThread();
		Thread rth = new Thread(th);
		rth.start();
	}

	@Override
	public void configEngine(Engine me) {
		
	}

	@Override
	public void configPlugin(Plugins me) {
		DruidPlugin plugin = new DruidPlugin(PropKit.get("jdbcUrl"),PropKit.get("username"),PropKit.get("password"));
	    me.add(plugin);

	    ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(plugin);
	    me.add(activeRecordPlugin);
	    
	    _MappingKit.mapping(activeRecordPlugin);
	    
		me.add(new EhCachePlugin());
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new LoginInterceptor());
		me.add(new AuthInterceptor());
		me.addGlobalActionInterceptor(new AdminAuthorityTemplateDirectiveInterceptor());
		me.addGlobalActionInterceptor(new UserAuthorityTemplateDirectiveInterceptor());
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("ctx"));
	}

	@Override
	public void afterJFinalStart() {
		//初始化操作类型信息
		OperaTypeService.init();
	}
	
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 80, "/");
	}
}
