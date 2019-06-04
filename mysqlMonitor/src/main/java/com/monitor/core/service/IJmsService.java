package com.monitor.core.service;

public interface IJmsService {
	
	/**发送消息到默认队列
	 * @param msg
	 */
	void send(String msg);
	/**发送消息到指定队列
	 * @param destinationName
	 * @param msg
	 */
	void send(String destinationName, String msg);
	
}
