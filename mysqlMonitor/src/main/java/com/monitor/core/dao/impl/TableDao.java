package com.monitor.core.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.core.dao.BaseDao;
import com.monitor.core.dao.ITableDao;

@Repository("tableDao")
public class TableDao extends BaseDao implements ITableDao{
	private final static Logger logger = LoggerFactory.getLogger(TableDao.class);
	@Resource(name="jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	@Override
	public List<Map<String,Object>> getTableId(String sql) {
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

	@Override
	public Map getTableKey(String table) {
		String sqlStr = "desc " + table;
		List<Map<String,Object>> list = this.jdbcTemplate.queryForList(sqlStr);
		String key = null;
		Map resultMap = new HashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			if (map.get("Key") != null && "PRI".equals(map.get("Key"))) {
				key = map.get("Field").toString();
				resultMap.put("key", key);
				resultMap.put("keyIndex", i);
				break;
			}
			
		}
		return resultMap;
	}

	@Override
	public List<String> getFields(String table) {
		String sqlStr = "desc " + table;
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sqlStr);
		List<String> fieldList = new ArrayList<String>();
		String key = null;
		for (Map<String, Object> map : list) {
			if (map.get("Field") != null) {
				fieldList.add(map.get("Field").toString());
			}
		}
		return fieldList;
	}

}
