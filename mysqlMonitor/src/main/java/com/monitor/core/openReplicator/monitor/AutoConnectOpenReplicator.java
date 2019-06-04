package com.monitor.core.openReplicator.monitor;

import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.or.OpenReplicator;

public class AutoConnectOpenReplicator extends OpenReplicator{
	private final static Logger logger = LoggerFactory.getLogger(AutoConnectOpenReplicator.class);
	@Override
	public void start() {
		try {
			if (!this.isRunning()) {
				this.reset();
				super.start();
				this.running.set(true);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}
	
	
	@Override
	public void stopQuietly(long timeout, TimeUnit unit) {
		super.stopQuietly(timeout, unit);
		if (this.getBinlogParser() != null) {
			// 重置, 当MySQL服务器进行restart/stop操作时进入该流程
			this.binlogParser.setParserListeners(null); // 这句比较关键，不然会死循环
		}
	}


	private void reset() {
		this.transport = null;
		this.binlogParser = null;
	}
	
}
