package com.monitor.core.openReplicator.monitor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.monitor.core.dao.IDBDao;
import com.monitor.core.datasource.DataSourceSwitch;
import com.monitor.core.openReplicator.listener.NotificationListener;
/**用于对数据库做心跳检查和开始数据监控
 * @author v-huanghanhui
 *
 */
@Component
public class OpenReplicatorMonitor {
	private final static Logger logger = LoggerFactory.getLogger(OpenReplicatorMonitor.class);
	@Value("${db.username}")
	private String username;
	@Value("${db.password}")
	private String password;
	@Value("${db.host}")
	private String host;
	@Value("${db.port}")
	private Integer port;
	//心跳
	private volatile boolean isDBAlive = true;
	private volatile boolean lastTimeDBStatus = true;
	private AutoConnectOpenReplicator openReplicator = null;
	@Resource
	private NotificationListener notificationListener;
	@Resource
	private IDBDao dbDao;
	
	
	public void start() {
		//开启心跳
		new Thread(new HeartBeat()).start();
		do {
			try {
				if (!isDBAlive) {
					//数据库断开
					lastTimeDBStatus = false;
					if (openReplicator != null) {
						openReplicator.stopQuietly(0, TimeUnit.SECONDS);
					}
					openReplicator = null;
					
				}else if (!lastTimeDBStatus) {
					this.updateBinlogPosition();
					lastTimeDBStatus = true;
					openReplicator = getOpenReplicator();
					updateBinlogPosition();
					openReplicator.start();
					logger.info("---------重新开启 binlog 监听---------");
					logger.info("host: " + host);
				}else {
					if (openReplicator == null) {
						openReplicator = getOpenReplicator();
						updateBinlogPosition();
						openReplicator.start();
						logger.info("---------开启 binlog 监听---------");
						logger.info("host: " + host);
					}
				}
				TimeUnit.SECONDS.sleep(2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} while (true);
		
	}
	
	private AutoConnectOpenReplicator getOpenReplicator() {
		AutoConnectOpenReplicator aor = new AutoConnectOpenReplicator();
		aor.setServerId(2);
		aor.setHost(host);
		aor.setUser(username);
		aor.setPassword(password);
		aor.setPort(port);
		aor.setBinlogEventListener(notificationListener);
		return aor;
	}
	
	/**
	 * 更新binlog位置
	 */
	private void updateBinlogPosition() {
		try {
			Map binlogInfo = dbDao.getBinlogInfo();
			if (binlogInfo != null) {
				String binlogName = binlogInfo.get("File").toString();
				Long position = Long.valueOf(binlogInfo.get("Position").toString());
				if (openReplicator != null) {
					openReplicator.setBinlogFileName(binlogName);
					openReplicator.setBinlogPosition(position);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新binlog position失败！");
		}
	}
//	心跳检测
	public class HeartBeat implements Runnable{
		@Override
		public void run() {
			while(true) {
				isDBAlive = dbDao.isAlive();
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
