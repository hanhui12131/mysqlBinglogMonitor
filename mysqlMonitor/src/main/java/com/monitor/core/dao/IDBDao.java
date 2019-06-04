package com.monitor.core.dao;

import java.util.Map;

public interface IDBDao {
	boolean isAlive();
	Map getBinlogInfo();
}
