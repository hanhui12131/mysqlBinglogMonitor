package com.monitor.core.openReplicator.listener;

import com.google.code.or.binlog.BinlogEventListener;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.impl.event.DeleteRowsEventV2;
import com.google.code.or.binlog.impl.event.TableMapEvent;
import com.google.code.or.binlog.impl.event.UpdateRowsEventV2;
import com.google.code.or.binlog.impl.event.WriteRowsEventV2;
import com.google.code.or.common.glossary.Pair;
import com.google.code.or.common.glossary.Row;
import com.google.code.or.common.glossary.column.StringColumn;
import com.monitor.core.datasource.DataSourceSwitch;
import com.monitor.core.service.IJmsService;
import com.monitor.core.service.ITableService;
import com.monitor.core.service.impl.TableService;
import com.monitor.core.utils.JacksonUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**binlog 事件监控
 * @author v-huanghanhui
 *
 */
@Component
public class NotificationListener implements BinlogEventListener {
	private ThreadLocal<Map> threadLocal = new ThreadLocal();
	private static Logger logger = LoggerFactory.getLogger(NotificationListener.class);
	@Resource(name = "tableService")
	private ITableService tableService;
	@Resource(name = "jmsService")
	private IJmsService jmsService;

	public void onEvents(BinlogEventV4 event) {
		if (event == null) {
			logger.error("binlog event is null");
			return;
		}
		if ((event instanceof WriteRowsEventV2)) {
			WriteRowsEventV2 insert = (WriteRowsEventV2) event;
			List<Row> rows = insert.getRows();
			Map map = (Map) this.threadLocal.get();
			String table = map.get("table").toString();
			String dbName = map.get("dbName").toString();
			DataSourceSwitch.setDataSourceKey(dbName);
			logger.debug("指定数据源" + dbName);
			List<Map<String, String>> list = this.tableService.getKeyMapByRow(rows, table, "insert", dbName);
			try {
				String json = JacksonUtil.toJson(list);
				this.jmsService.send(json);
				logger.debug(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ((event instanceof DeleteRowsEventV2)) {
			DeleteRowsEventV2 delete = (DeleteRowsEventV2) event;
			List<Row> rows = delete.getRows();
			Map map = (Map) this.threadLocal.get();
			String table = map.get("table").toString();
			String dbName = map.get("dbName").toString();
			DataSourceSwitch.setDataSourceKey(dbName);
			logger.debug("指定数据源" + dbName);
			List<Map<String, String>> list = this.tableService.getKeyMapByRow(rows, table, "delete", dbName);
			try {
				String json = JacksonUtil.toJson(list);
				this.jmsService.send(json);
				logger.debug(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ((event instanceof UpdateRowsEventV2)) {
			UpdateRowsEventV2 update = (UpdateRowsEventV2) event;
			List<Pair<Row>> rows = update.getRows();
			Pair<Row> pair = (Pair) rows.get(0);
			Map map = (Map) this.threadLocal.get();
			String table = map.get("table").toString();
			String dbName = map.get("dbName").toString();
			DataSourceSwitch.setDataSourceKey(dbName);
			logger.debug("指定数据源" + dbName);
			List<Map<String, String>> list = this.tableService.getKeyMap(rows, table, "update", dbName);
			try {
				String json = JacksonUtil.toJson(list);
				this.jmsService.send(json);
				logger.debug(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ((event instanceof TableMapEvent)) {
			TableMapEvent tableMapEvent = (TableMapEvent) event;
			Map map = new HashMap();
			this.threadLocal.set(map);
			String tableName = tableMapEvent.getTableName().toString();
			String dbName = tableMapEvent.getDatabaseName().toString();
			map.put("table", tableName);
			map.put("dbName", dbName);
		}
	}
}
