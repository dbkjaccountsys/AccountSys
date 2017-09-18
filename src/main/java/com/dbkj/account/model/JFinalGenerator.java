package com.dbkj.account.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

public class JFinalGenerator {
	
	public static DataSource getDataSource(){
		PropKit.use("config.properties");
		DruidPlugin plugin = new DruidPlugin(PropKit.get("jdbcUrl"),PropKit.get("username"),PropKit.get("password"));
		plugin.start();
		return plugin.getDataSource();
	}
	
	public static void main(String[] args){
		// base model �?使用的包�?
		String baseModelPackageName = "com.dbkj.account.model.base";
		
//		System.out.println(PathKit.getWebRootPath());
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/dbkj/account/model/base";
//		
		// model �?使用的包�? (MappingKit 默认使用的包�?)
		String modelPackageName = "com.dbkj.account.model";
		// model 文件保存路径 (MappingKit �? DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成�?
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 设置数据库方�?
		gernerator.setDialect(new MysqlDialect());
		// 添加不需要生成的表名
		//gernerator.addExcludedTable("adv");
//		gernerator.addExcludedTable("calllogs_20160624","calllogs_20160627","calllogs_20160628","calllogs_20160629","calllogs_20160630","calllogs_20160704","calllogs_20160705","calllogs_20160706","calllogs_20160712");
		// 设置是否�? Model 中生�? dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置�?要被移除的表名前�?用于生成modelName。例如表�? "osc_user"，移除前�? "osc_"后生成的model名为 "User"而非 OscUser
		//gernerator.setRemovedTableNamePrefixes("t_");
		// 生成
		gernerator.generate();
		
//		DataSource dataSource=getDataSource();
//		Connection connection = null;
//		try {
//			 connection = dataSource.getConnection();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		if(connection!=null){
//			try {
//				Statement statement = connection.createStatement();
//				ResultSet resultSet = statement.executeQuery("show tables");
//				while(resultSet.next()){
//					String tableName = resultSet.getString("Tables_in_dbtec");
//					System.out.println(tableName);
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
	}
}
