package com.monitor.core.dao;

import java.util.List;
import java.util.Map;

public interface ITableDao {
	
	List<Map<String,Object>> getTableId(String sql);
	Map getTableKey(String table);
	List<String> getFields(String table);
}
