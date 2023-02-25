package com.julong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.julong.service.HelloService;

/**
 * dubbo配置
 * @author julong
 * @date 2020年2月29日 下午3:23:17
 * @desc 
 */
@Configuration
public class ProviderDubboConfig {

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
		applicationConfig.setName("alibaba-dubbo-learn-annotation-provider-example");
		return applicationConfig;
	}
	
	//注册中心配置。对应的配置类： org.apache.dubbo.config.RegistryConfig。同时如果有多个不同的注册中心，可以声明多个 <dubbo:registry> 标签，并在 <dubbo:service> 或 <dubbo:reference> 的 registry 属性指定使用的注册中心。
	
	/**
	 * 注册中心配置
	 * @return
	 * @author julong
	 * @date 2020年2月29日 下午3:01:09
	 * @desc <dubbo:registry address="zookeeper://192.168.10.222:2181" />
	 */
	@Bean
	public RegistryConfig registryConfig(){
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setProtocol("zookeeper");//注册中心名称
		registryConfig.setAddress("192.168.10.222:2181");
		return registryConfig;
	}
	
	
	//服务提供者协议配置。对应的配置类： org.apache.dubbo.config.ProtocolConfig。同时，如果需要支持多协议，可以声明多个 <dubbo:protocol> 标签，并在 <dubbo:service> 中通过 protocol 属性指定使用的协议。
	
	/**
	 * 服务提供者协议配置
	 * @return
	 * @author julong
	 * @date 2020年2月29日 下午3:03:55
	 * @desc <dubbo:protocol name="dubbo" port="20880" />
	 */
	@Bean
	public ProtocolConfig protocolConfig(){
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName("dubbo");//协议名称
		protocolConfig.setPort(20882);//dubbo协议缺省端口为20880，rmi协议缺省端口为1099，http和hessian协议缺省端口为80；如果没有配置port，则自动采用默认端口，如果配置为-1，则会分配一个没有被占用的端口。Dubbo 2.4.0+，分配的端口在协议缺省端口的基础上增长，确保端口段可控。
		return protocolConfig;
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
	
	
	//服务提供者暴露服务配置。对应的配置类：org.apache.dubbo.config.ServiceConfig
	
	/**
	 * @return
	 * @author julong
	 * @date 2020年2月29日 下午3:15:00
	 * @desc <dubbo:service  interface="com.julong.service.HelloService" ref="helloService"  version="1.0.0"/>
	 */
	@Bean
	public ServiceConfig<HelloService> helloServiceConfig(HelloService helloServiceImpl){
		ServiceConfig<HelloService> serviceConfig = new ServiceConfig<HelloService>();
		serviceConfig.setInterface(HelloService.class);//服务接口名
		serviceConfig.setRef(helloServiceImpl);//服务对象实现引用
//		serviceConfig.setVersion("1.0.0");//服务版本，建议使用两位数字版本，如：1.0，通常在接口不兼容时版本号才需要升级
//		serviceConfig.setLoadbalance(loadbalance);//负载均衡策略，可选值：random,roundrobin,leastactive，分别表示：随机，轮询，最少活跃调用
//		serviceConfig.setMethods(methods);//可以指定方法
		return serviceConfig;
	}
	
	
}
