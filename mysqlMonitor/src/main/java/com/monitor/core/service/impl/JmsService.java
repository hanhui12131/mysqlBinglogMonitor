package com.monitor.core.service.impl;


import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.monitor.core.service.IJmsService;



@Service("jmsService")
public class JmsService implements IJmsService{
	private static final Logger logger = LoggerFactory.getLogger(JmsService.class);
	@Resource
	private JmsTemplate jmsTemplate;
	
	@Override
	public void send(final String msg) {
		try {
			jmsTemplate.send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage message = session.createTextMessage(msg);
					logger.debug("--发送消息成功--  ");
					logger.debug(msg);
					return message;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send(String destinationName, final String msg) {
		try {
			jmsTemplate.send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage message = session.createTextMessage(msg);
					logger.debug("--发送消息成功--  ");
					logger.debug(msg);
					return message;
				}
			});		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}