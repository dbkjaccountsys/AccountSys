package com.dbkj.account.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.dbkj.account.controller.ForgetPasswordController;
import com.dbkj.account.controller.VertifyCodeController;
import com.dbkj.account.interceptor.*;
import com.dbkj.account.model._MappingKit;
import com.dbkj.account.service.OperaTypeService;
import com.dbkj.account.sys.Config;
import com.dbkj.account.sys.EmailResetCleanThread;
import com.dbkj.account.sys.MapValueThread;
import com.dbkj.account.sys.PayThread;
import com.dbkj.account.sys.SysRoutes;
import com.dbkj.account.sys.inter.LoginInter;
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
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig extends JFinalConfig{

	private static final Logger logger= LoggerFactory.getLogger(AppConfig.class);

	@Override
	public void configConstant(Constants me) {
		if(logger.isInfoEnabled()) logger.info("configConstant 缓存 properties");
		PropKit.use("config.properties");
	    //设置当前是否为开发模式
		if(logger.isInfoEnabled()) logger.info("configConstant 设置是否开发模式");
	    me.setDevMode(PropKit.getBoolean("devMode"));
	    //设置上传最大限制尺寸
		if(logger.isInfoEnabled()) logger.info("configConstant 设置上传文件大小限制");
	    me.setMaxPostSize(1024*1024*4);
		if(logger.isInfoEnabled()) logger.info("configConstant 设置视图类型");
	    me.setViewType(ViewType.FREE_MARKER);
		if(logger.isInfoEnabled()) logger.info("configConstant 视图error page设置");
	    me.setErrorView(401, "/pages/other/401.html");
	    me.setErrorView(404, "/pages/other/404.html");
	    me.setErrorView(500, "/pages/other/500.html");
	    //设置token缓存
		me.setTokenCache(new FromTokenByEhcache());
	}

	@Override
	public void configRoute(Routes me) {
		me.add(new SysRoutes());
		
		me.add("/vertifyCode",VertifyCodeController.class);
		me.add("/forget",ForgetPasswordController.class);
		me.add(new ManageRoute());
	}

	@Override
	public void configEngine(Engine me) {
		
	}

	@Override
	public void configPlugin(Plugins me) {
		String dbType=PropKit.get("dbType","mysql");
		if(logger.isInfoEnabled()) logger.info("configPlugin 配置Druid数据库连接池连接属性");
		DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbcUrl"),PropKit.get("username"),PropKit.get("password"));

		if(logger.isInfoEnabled()) logger.info("configPlugin 配置Druid数据库连接池大小");
		druidPlugin.set(PropKit.getInt("dataSource.pool.initialSize",10),
				PropKit.getInt("dataSource.pool.minIdle",10),
				PropKit.getInt("dataSource.pool.maxActive",100));

		if(logger.isInfoEnabled()) logger.info("configPlugin 配置Druid数据库连接池过滤器配制");
		druidPlugin.addFilter(new StatFilter());
		WallFilter wall = new WallFilter();
		wall.setDbType(dbType);
		WallConfig config = new WallConfig();
		config.setFunctionCheck(false); // 支持数据库函数
		wall.setConfig(config);
		druidPlugin.addFilter(wall);

	    me.add(druidPlugin);

	    ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(druidPlugin);
	    boolean devMode=PropKit.getBoolean("devMode",false);
		activeRecordPlugin.setDevMode(devMode); // 设置开发模式
		activeRecordPlugin.setShowSql(devMode); // 是否显示SQL
		activeRecordPlugin.setContainerFactory(new CaseInsensitiveContainerFactory(true)); // 大小写不敏感

		if("mysql".equalsIgnoreCase(dbType)){
			if(logger.isInfoEnabled()) logger.info("configPlugin 使用数据库类型是 mysql");
			activeRecordPlugin.setDialect(new MysqlDialect());
		}
	    me.add(activeRecordPlugin);
	    
	    _MappingKit.mapping(activeRecordPlugin);
	    
		me.add(new EhCachePlugin());
		
		//配置Redis
		RedisPlugin redis=new RedisPlugin("AccountSys",
		                PropKit.get("redis.host","127.0.0.1"),
		                PropKit.getInt("redis.port",6379));
		me.add(redis);

		//sql文件扫描加载
		me.add(new SqlScanPlugin(PropKit.get("sqlPath","sql")));
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new XSSInteceptor());
		me.add(new LoginInterceptor());
		me.add(new AuthInterceptor());
		me.add(new AdminAuthorityTemplateDirectiveInterceptor());
		me.add(new UserAuthorityTemplateDirectiveInterceptor());
		me.add(new LoginInter());
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("ctx"));
	}

	@Override
	public void afterJFinalStart() {
		//初始化操作类型信息
		OperaTypeService.init();
		
		Config.init();
		
		MapValueThread th1 = new MapValueThread();
		Thread t1 = new Thread(th1);
		t1.start();
		
		EmailResetCleanThread th2 = new EmailResetCleanThread();
		Thread t2 = new Thread(th2);
		t2.start();
		
		PayThread th3 = new PayThread();
		Thread t3 = new Thread(th3);
		t3.start();
	}
	
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 80, "/");
	}
}
