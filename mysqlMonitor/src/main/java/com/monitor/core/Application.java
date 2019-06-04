package com.monitor.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.monitor.core.openReplicator.monitor.OpenReplicatorMonitor;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Application.class);
		springApplication.addListeners(new ApplicationListener<ContextRefreshedEvent>() {
			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				//启动binlog 日志监听程序入口
				OpenReplicatorMonitor om = (OpenReplicatorMonitor) event.getApplicationContext().getBean("openReplicatorMonitor");
				om.start();
			}
		});
		springApplication.run(args);
	}

}
