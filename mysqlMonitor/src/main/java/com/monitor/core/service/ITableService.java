package com.monitor.core.service;

import java.util.List;
import java.util.Map;

import com.google.code.or.common.glossary.Pair;
import com.google.code.or.common.glossary.Row;

public interface ITableService {
	public List<Map<String, String>> getKeyMap(List<Pair<Row>> pairs, String table,String type, String db);
	public List<Map<String, String>> getKeyMapByRow(List<Row> rows, String table,String type,String db);
}
