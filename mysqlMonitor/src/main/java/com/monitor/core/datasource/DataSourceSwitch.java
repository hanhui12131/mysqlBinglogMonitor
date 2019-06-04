package com.monitor.core.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**动态数据源切换
 * @author v-huanghanhui
 *
 */
public class DataSourceSwitch extends AbstractRoutingDataSource{

	private final static Logger logger = LoggerFactory.getLogger(DataSourceSwitch.class);
	
	private static ThreadLocal<String> datasourceKey = new ThreadLocal<String>();
	
	
	public static void clearDataSourceType() {
		datasourceKey.remove();
	}
	
	public static void setDataSourceKey(String key) {
		datasourceKey.set(key);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		String key = datasourceKey.get();
		return key;
	}
}
