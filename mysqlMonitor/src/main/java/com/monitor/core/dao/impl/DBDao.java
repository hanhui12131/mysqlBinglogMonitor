package com.monitor.core.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.core.dao.BaseDao;
import com.monitor.core.dao.IDBDao;

@Repository
public class DBDao extends BaseDao implements IDBDao{
	private static final Logger logger = LoggerFactory.getLogger(DBDao.class);
	@Resource(name="jdbcTemplate")
	private JdbcTemplate jdbcTemplate; 
	@Resource(name="test")
	private DataSource test;
	
	@Override
	public boolean isAlive() {
		String sql = "select 1";
		try {
			List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
			if (!list.isEmpty()) {
				return true; 
			}
		} catch (Exception e) {
			logger.error("数据库连接失败！！");
		}
		return false;
	}

	@Override
	public Map getBinlogInfo() {
		String sql ="show master status";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

}
