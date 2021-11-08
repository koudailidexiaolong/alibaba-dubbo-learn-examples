package com.julong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.RegistryConfig;

@Configuration
public class ConsumerDubboConfig {

	//应用信息配置。对应的配置类：org.apache.dubbo.config.ApplicationConfig
	/**
	 * 配置服务信息
	 * @return
	 * @author julong
	 * @date 2020年2月29日 下午2:57:21
	 * @desc 	<dubbo:application name="hello-world-app" />
	 */
	@Bean
	public ApplicationConfig applicationConfig(){
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName("alibaba-dubbo-learn-annotation-consumer-example");
		return applicationConfig;
	}
	
	//注册中心配置。对应的配置类： org.apache.dubbo.config.RegistryConfig。同时如果有多个不同的注册中心，可以声明多个 <dubbo:registry> 标签，并在 <dubbo:service> 或 <dubbo:reference> 的 registry 属性指定使用的注册中心。
	
	/**
	 * 注册中心配置
	 * @return
	 * @author julong
	 * @date 2020年2月29日 下午3:01:09
	 * @desc <dubbo:registry address="zookeeper://192.168.10.132:2181" />
	 */
	@Bean
	public RegistryConfig registryConfig(){
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setProtocol("zookeeper");//注册中心名称
		registryConfig.setAddress("192.168.10.132:2181");
		registryConfig.setCheck(false);
		return registryConfig;
	}
		
	
	//监控中心配置。对应的配置类： org.apache.dubbo.config.MonitorConfig
	
	/**
	 * 监控中心配置
	 * @return
	 * @author julong
	 * @date 2020年2月29日 下午3:10:19
	 * @desc   <dubbo:monitor protocol="registry"></dubbo:monitor>
	 */
	@Bean
	public MonitorConfig monitorConfig(){
		MonitorConfig monitorConfig = new MonitorConfig();
		monitorConfig.setProtocol("registry");//监控中心协议，如果为protocol="registry"，表示从注册中心发现监控中心地址，否则直连监控中心。
		return monitorConfig;
	}
	
	
		
}
