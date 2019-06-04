package com.monitor.core.conf;

import com.monitor.core.datasource.DataSourceSwitch;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Config {
	Logger logger = LoggerFactory.getLogger(Config.class);

	
	@Resource(name="test")
	private DataSource test;
	@Resource(name="test2")
	private DataSource test2;
	
	private Class dataSourceClass = PooledDataSource.class;

	@Bean(name="test")
	@ConfigurationProperties(prefix="db1")
	public DataSource crateDataSource() {
		DataSource dataSource = DataSourceBuilder.create().type(dataSourceClass).build();
		return dataSource;
	}
	
	@Bean(name="test2")
	@ConfigurationProperties(prefix="db2")
	public DataSource crateDataSource2() {
		DataSource dataSource = DataSourceBuilder.create().type(dataSourceClass).build();
		return dataSource;
	}
	
	/**多数据源切换
	 * @return
	 */
	@Bean(name="dataSourceSwitch")
	public DataSource getDataSourceSwitch() {
		DataSourceSwitch dataSourceSwitch = new DataSourceSwitch();
		//设置目标数据源
		Map targetDataSource = new HashMap<String, Object>();
		targetDataSource.put("test", test);
		targetDataSource.put("test2", test2);
		dataSourceSwitch.setTargetDataSources(targetDataSource);
		//设置默认数据源
		dataSourceSwitch.setDefaultTargetDataSource(test);
		return dataSourceSwitch;
	}
	
	@Bean(name="jdbcTemplate")
	public JdbcTemplate getJdbcTemplate(@Qualifier("dataSourceSwitch")DataSource dataSourceSwitch) {
		return new JdbcTemplate(dataSourceSwitch);
	}
	
}
