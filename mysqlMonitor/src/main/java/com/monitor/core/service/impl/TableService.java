package com.monitor.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.code.or.common.glossary.Pair;
import com.google.code.or.common.glossary.Row;
import com.monitor.core.dao.ITableDao;
import com.monitor.core.service.ITableService;



@Service("tableService")
public class TableService implements ITableService{
	
	private static final Logger logger = LoggerFactory.getLogger(TableService.class);
	@Resource(name="tableDao")
	private ITableDao tableDao;
	
	/**获取跟新数据前主键的信息
	 * @param pairs
	 * @param table
	 * @param type
	 * @return
	 */
	public List<Map<String, String>> getKeyMap(List<Pair<Row>> pairs, String table,String type, String db){
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		List<String> fields = tableDao.getFields(table);
		Map keyMap = tableDao.getTableKey(table);
		String key = keyMap.get("key").toString();
		int keyIndex = Integer.valueOf(keyMap.get("keyIndex").toString());
		for (Pair<Row> pair : pairs) {
			Map map = new HashMap<String, String>();
			String keyValue = pair.getBefore().getColumns().get(keyIndex).toString();
			map.put("keyValue", keyValue);
			map.put("key", key);
			map.put("type", type);
			map.put("table", table);
			map.put("db", db);
			list.add(map);
		}
		return list;
	}
	/**获取跟新数据前主键的信息
	 * @param pairs
	 * @param table
	 * @param type
	 * @return
	 */
	public List<Map<String, String>> getKeyMapByRow(List<Row> rows, String table,String type,String db){
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		List<String> fields = tableDao.getFields(table);
		Map keyMap = tableDao.getTableKey(table);
		//获取主键名称
		String key = keyMap.get("key").toString();
		//获取主键位置
		int keyIndex = Integer.valueOf(keyMap.get("keyIndex").toString());
		for (Row row : rows) {
			Map map = new HashMap<String, String>();
			String keyValue = row.getColumns().get(keyIndex).toString();
			map.put("keyValue", keyValue);
			map.put("key", key);
			map.put("type", type);
			map.put("table", table);
			map.put("db", db);
			list.add(map);
			
		}
		return list;
	}
}
