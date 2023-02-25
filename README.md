# alibaba-dubbo-learn-examples
alibaba-dubbo-learn-examples dubbo 学习示例

## 背景

随着互联网的发展，网站应用的规模不断扩大，常规的垂直应用架构已无法应对，分布式服务架构以及流动计算架构势在必行，亟需一个治理系统确保架构有条不紊的演进。

![image-20230223160152889](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223160152889.png)

### 单一应用架构

当网站流量很小时，只需一个应用，将所有功能都部署在一起，以减少部署节点和成本。此时，用于简化增删改查工作量的数据访问框架(ORM)是关键。

### 垂直应用架构

当访问量逐渐增大，单一应用增加机器带来的加速度越来越小，提升效率的方法之一是将应用拆成互不相干的几个应用，以提升效率。此时，用于加速前端页面开发的Web框架(MVC)是关键。

### 分布式服务架构

当垂直应用越来越多，应用之间交互不可避免，将核心业务抽取出来，作为独立的服务，逐渐形成稳定的服务中心，使前端应用能更快速的响应多变的市场需求。此时，用于提高业务复用及整合的分布式服务框架(RPC)是关键。

### 流动计算架构

当服务越来越多，容量的评估，小服务资源的浪费等问题逐渐显现，此时需增加一个调度中心基于访问压力实时管理集群容量，提高集群利用率。此时，用于提高机器利用率的资源调度和治理中心(SOA)是关键。

## 需求

![image-20230223161403922](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223161403922.png)

在大规模服务化之前，应用可能只是通过 RMI 或 Hessian 等工具，简单的暴露和引用远程服务，通过配置服务的URL地址进行调用，通过 F5 等硬件进行负载均衡。

### 服务配置

当服务越来越多时，服务 URL 配置管理变得非常困难，F5 硬件负载均衡器的单点压力也越来越大。 此时需要一个服务注册中心，动态地注册和发现服务，使服务的位置透明。并通过在消费方获取服务提供方地址列表，实现软负载均衡和 Failover，降低对 F5 硬件负载均衡器的依赖，也能减少部分成本。

### 服务依赖

当进一步发展，服务间依赖关系变得错踪复杂，甚至分不清哪个应用要在哪个应用之前启动，架构师都不能完整的描述应用的架构关系。 这时，需要自动画出应用间的依赖关系图，以帮助架构师理清关系。

### 服务容量

接着，服务的调用量越来越大，服务的容量问题就暴露出来，这个服务需要多少机器支撑？什么时候该加机器？ 为了解决这些问题，第一步，要将服务现在每天的调用量，响应时间，都统计出来，作为容量规划的参考指标。其次，要可以动态调整权重，在线上，将某台机器的权重一直加大，并在加大的过程中记录响应时间的变化，直到响应时间到达阈值，记录此时的访问量，再以此访问量乘以机器数反推总容量。



## 架构

![image-20230223161643005](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223161643005.png)

### 节点角色说明

| 节点      | 角色说明                               |
| --------- | -------------------------------------- |
| Provider  | 暴露服务的服务提供方                   |
| Consumer  | 调用远程服务的服务消费方               |
| Registry  | 服务注册与发现的注册中心               |
| Monitor   | 统计服务的调用次数和调用时间的监控中心 |
| Container | 服务运行容器                           |

### 调用关系说明

```txt
0.服务容器负责启动，加载，运行服务提供者。
1.服务提供者在启动时，向注册中心注册自己提供的服务。
2.服务消费者在启动时，向注册中心订阅自己所需的服务。
3.注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
4.服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
5.服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心。
```

Dubbo 架构具有以下几个特点，分别是连通性、健壮性、伸缩性、以及向未来架构的升级性。

### 连通性

```txt
注册中心负责服务地址的注册与查找，相当于目录服务，服务提供者和消费者只在启动时与注册中心交互，注册中心不转发请求，压力较小
监控中心负责统计各服务调用次数，调用时间等，统计先在内存汇总后每分钟一次发送到监控中心服务器，并以报表展示
服务提供者向注册中心注册其提供的服务，并汇报调用时间到监控中心，此时间不包含网络开销
服务消费者向注册中心获取服务提供者地址列表，并根据负载算法直接调用提供者，同时汇报调用时间到监控中心，此时间包含网络开销
注册中心，服务提供者，服务消费者三者之间均为长连接，监控中心除外
注册中心通过长连接感知服务提供者的存在，服务提供者宕机，注册中心将立即推送事件通知消费者
注册中心和监控中心全部宕机，不影响已运行的提供者和消费者，消费者在本地缓存了提供者列表
注册中心和监控中心都是可选的，服务消费者可以直连服务提供者
```
### 健壮性

```txt
监控中心宕掉不影响使用，只是丢失部分采样数据
数据库宕掉后，注册中心仍能通过缓存提供服务列表查询，但不能注册新服务
注册中心对等集群，任意一台宕掉后，将自动切换到另一台
注册中心全部宕掉后，服务提供者和服务消费者仍能通过本地缓存通讯
服务提供者无状态，任意一台宕掉后，不影响使用
服务提供者全部宕掉后，服务消费者应用将无法使用，并无限次重连等待服务提供者恢复
```
### 伸缩性

```txt
注册中心为对等集群，可动态增加机器部署实例，所有客户端将自动发现新的注册中心
服务提供者无状态，可动态增加机器部署实例，注册中心将推送新的服务提供者信息给消费者
```


## dubbo基于xml的配置示例

Dubbo 采用全 Spring 配置方式，透明化接入应用，对应用没有任何 API 侵入，只需用 Spring 加载 Dubbo 的配置即可，Dubbo 基于 Spring 的 Schema 扩展 进行加载



pom文件配置：

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo</artifactId>
    <version>2.6.9</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.32.Final</version>
</dependency>
```



### 服务端-服务提供者

#### alibaba-dubbo-learn-xml-server-example

#### 1.定义服务接口

```java
package com.julong.dubbo.service;

/**
 * 测试接口
 * @author julong
 * @date 2021年11月27日 上午11:04:55
 * @desc 
 */
public interface HelloService {

	public abstract String sayHello(String message);
}

```

#### 2.在服务提供方实现接口

```java
package com.julong.dubbo.service.impl;

import com.julong.dubbo.service.HelloService;

public class HelloServiceImpl implements HelloService {

	@Override
	public String sayHello(String message) {
		// TODO Auto-generated method stub
		return "dubbo :" +message;
	}

}

```

#### 3.用 Spring 配置声明暴露服务

dubbo-server.xml 对应 服务 端的 provider

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="alibaba-dubbo-learn-xml-server-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露服务地址  N/A 代表不需要注册中心     multicast://224.5.6.7:1234-->
    <dubbo:registry address="N/A" />
 
    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="com.julong.dubbo.service.HelloService" ref="helloService" />
 
    <!-- 和本地bean一样实现服务 -->
    <bean id="helloService" class="com.julong.dubbo.service.impl.HelloServiceImpl" />
</beans>

```

#### 4.加载 Spring 配置

```java
package com.julong.dubbo.main;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务提供方
 * @author julong
 * @date 2021年10月22日 下午9:57:34
 * @desc 
 */
public class ProviderMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-server.xml"});
		context.start();
		try {
			System.out.println("服务启动");
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 按任意键退出
	}

}

```

启动信息：

```log
二月 23, 2023 4:36:37 下午 org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
信息: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@307fc620: startup date [Thu Feb 23 16:36:37 CST 2023]; root of context hierarchy
二月 23, 2023 4:36:37 下午 org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
信息: Loading XML bean definitions from class path resource [dubbo-server.xml]
二月 23, 2023 4:36:37 下午 com.alibaba.dubbo.common.logger.LoggerFactory info
信息: using logger: com.alibaba.dubbo.common.logger.jcl.JclLoggerAdapter
二月 23, 2023 4:36:38 下午 com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory warn
警告:  [DUBBO] No spring extension (bean) named:defaultCompiler, try to find an extension (bean) of type java.lang.String, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:36:38 下午 com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory warn
警告:  [DUBBO] No spring extension (bean) named:defaultCompiler, type:java.lang.String found, stop get bean., dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:36:38 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] The service ready on spring started. service: com.julong.dubbo.service.HelloService, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:36:38 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] Export dubbo service com.julong.dubbo.service.HelloService to local registry, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:36:38 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] Export dubbo service com.julong.dubbo.service.HelloService to url dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&bind.ip=192.168.10.128&bind.port=20880&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5128&side=provider&timestamp=1677141398469, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:36:39 下午 com.alibaba.dubbo.remoting.transport.AbstractServer info
信息:  [DUBBO] Start NettyServer bind /0.0.0.0:20880, export /192.168.10.128:20880, dubbo version: 2.6.9, current host: 192.168.10.128
服务启动
```





### 客户端-服务消费者

#### alibaba-dubbo-learn-xml-client-example

#### 1.通过 Spring 配置引用远程服务

dubbo-client.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 

 
    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="alibaba-dubbo-learn-xml-client-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露发现服务地址 multicast://224.5.6.7:1234-->
    <dubbo:registry address="N/A" />
    
 	 <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 
           生成远程服务代理，可以和本地bean一样使用demoService  无注册中心的方法 使用url 配置
            生成远程服务代理，dubbo:reference 可以和本地bean一样使用helloService 
   	check="false" 忽略检查   服务提供者不存在 则不报错
   	check="true" 检查  服务提供者 如果不存在则报错
   	timeout 服务方法调用超时时间(毫秒)
    retries	远程服务调用重试次数，不包括第一次调用，不需要重试请设为0   新增不要设置 重试次数
    version 服务版本，与服务提供者的版本一致   *代表 随机
    -->
    <dubbo:reference id="helloService" interface="com.julong.dubbo.service.HelloService" url="dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService" />
</beans>

```

#### 2.声明接口

```java
package com.julong.dubbo.service;

/**
 * 测试接口
 * @author julong
 * @date 2021年11月27日 上午11:05:13
 * @desc 
 */
public interface HelloService {

	public abstract String sayHello(String message);
}

```



#### 3.加载Spring配置，并调用远程服务

```java
package com.julong.dubbo.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.julong.dubbo.service.HelloService;

/**
 * 服务调用方
 * @author julong
 * @date 2021年10月22日 下午9:59:52
 * @desc 
 */
public class CusumerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"dubbo-client.xml"});
		context.start();
		HelloService helloService = (HelloService)context.getBean("helloService"); // 获取远程服务代理
		String hello = helloService.sayHello("hello world"); // 执行远程方法
		System.out.println( hello ); // 显示调用结果
	}

}

```

调用日志：

```log
二月 23, 2023 4:39:29 下午 org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
信息: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@5d1e8050: startup date [Thu Feb 23 16:39:29 CST 2023]; root of context hierarchy
二月 23, 2023 4:39:29 下午 org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
信息: Loading XML bean definitions from class path resource [dubbo-client.xml]
二月 23, 2023 4:39:30 下午 com.alibaba.dubbo.common.logger.LoggerFactory info
信息: using logger: com.alibaba.dubbo.common.logger.jcl.JclLoggerAdapter
二月 23, 2023 4:39:30 下午 com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory warn
警告:  [DUBBO] No spring extension (bean) named:defaultCompiler, try to find an extension (bean) of type java.lang.String, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:39:30 下午 com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory warn
警告:  [DUBBO] No spring extension (bean) named:defaultCompiler, type:java.lang.String found, stop get bean., dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:39:31 下午 com.alibaba.dubbo.remoting.transport.AbstractClient info
信息:  [DUBBO] Successed connect to server /192.168.10.128:20880 from NettyClient 192.168.10.128 using dubbo version 2.6.9, channel is NettyChannel [channel=[id: 0x0ad7fcad, L:/192.168.10.128:54213 - R:/192.168.10.128:20880]], dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:39:31 下午 com.alibaba.dubbo.remoting.transport.AbstractClient info
信息:  [DUBBO] Start NettyClient WIN-GMBJ6AGDUPM/192.168.10.128 connect to the server /192.168.10.128:20880, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 4:39:31 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] Refer dubbo service com.julong.dubbo.service.HelloService from url dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-xml-client-example&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=4604&register.ip=192.168.10.128&side=consumer&timestamp=1677141570720, dubbo version: 2.6.9, current host: 192.168.10.128
dubbo :hello world

```



## 策略成熟度

![image-20230223164853536](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223164853536.png)

![image-20230223165004464](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223165004464.png)

![image-20230223165028238](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223165028238.png)

![image-20230223165309985](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223165309985.png)

![image-20230223165145833](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223165145833.png)



## XML 配置属性

![image-20230223165651773](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223165651773.png)

![image-20230223170028799](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223170028799.png)

### 不同粒度配置的覆盖关系

以 timeout 为例，下图显示了配置的查找顺序，其它 retries, loadbalance, actives 等类似：

    方法级优先，接口级次之，全局配置再次之。
    如果级别一样，则消费方优先，提供方次之。

其中，服务提供方配置，通过 URL 经由注册中心传递给消费方。

![image-20230223170235349](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223170235349.png)

建议由服务提供方设置超时，因为一个方法需要执行多长时间，服务提供方更清楚，如果一个消费方同时引用多个服务，就不需要关心每个服务的超时设置）。

理论上 ReferenceConfig 中除了interface这一项，其他所有配置项都可以缺省不配置，框架会自动使用ConsumerConfig，ServiceConfig, ProviderConfig等提供的缺省配置。

    1 2.1.0 开始支持，注意声明：xmlns:p="http://www.springframework.org/schema/p" 
    
    2 引用缺省是延迟初始化的，只有引用被注入到其它 Bean，或被 getBean() 获取，才会初始化。如果需要饥饿加载，即没有人引用也立即生成动态代理，可以配置：<dubbo:reference ... init="true" /> 



## dubbo 监控中心下载和配置

下载地址：https://github.com/koudailidexiaolong/dubbo-admin.git

编译控制台管理 mvn package

![image-20230225204008500](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230225204008500.png)

## dubbo 基于zookeeper的注册中心示例

### pom.xml引入依赖

```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.3.3</version>
</dependency>
<dependency>
    <groupId>com.github.sgroschupf</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.1</version>
</dependency>


<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo</artifactId>
    <version>2.6.9</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.32.Final</version>
</dependency>
```



### 服务端-服务提供者

#### alibaba-dubbo-learn-zookeeper-xml-server-example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="alibaba-dubbo-learn-zookeeper-xml-server-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露服务地址  N/A 代表不需要注册中心     multicast://224.5.6.7:1234 zookeeper://192.168.10.222:12181-->
    <dubbo:registry protocol="zookeeper" address="192.168.10.222:2181" client="zkclient" />
    <!-- 集群配置：
    	address="10.20.153.10:2181,10.20.153.11:2181,10.20.153.12:2181"
    	address="zookeeper://10.20.153.10:2181?backup=10.20.153.11:2181,10.20.153.12:2181"
         -->
 
    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="com.julong.dubbo.service.HelloService" ref="helloService" />
 
    <!-- 和本地bean一样实现服务 -->
    <bean id="helloService" class="com.julong.dubbo.service.impl.HelloServiceImpl" />
</beans>

```

代码参考dubbo的xml示例

### 客户端-服务消费者

#### alibaba-dubbo-learn-zookeeper-xml-client-example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 

 
    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="alibaba-dubbo-learn-zookeper-xml-client-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露发现服务地址 multicast://224.5.6.7:1234 zookeeper://192.168.10.222:12181 -->
    <dubbo:registry address="zookeeper://192.168.10.222:2181"  client="zkclient"/>
    
 	 <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 
           生成远程服务代理，可以和本地bean一样使用demoService  无注册中心的方法 使用url 配置
            生成远程服务代理，dubbo:reference 可以和本地bean一样使用helloService 
   	check="false" 忽略检查   服务提供者不存在 则不报错
   	check="true" 检查  服务提供者 如果不存在则报错
   	timeout 服务方法调用超时时间(毫秒)
    retries	远程服务调用重试次数，不包括第一次调用，不需要重试请设为0   新增不要设置 重试次数
    version 服务版本，与服务提供者的版本一致   *代表 随机
    -->
    <dubbo:reference id="helloService" interface="com.julong.dubbo.service.HelloService"  />
</beans>

```

代码参考dubbo的xml示例

### zookeeper 配置中心

```txt
1.外部化配置。启动配置的集中式存储 （简单理解为 dubbo.properties 的外部化存储）。
2.服务治理。服务治理规则的存储与通知。
3.为了兼容 2.6.x 版本配置，在使用 Zookeeper 作为注册中心，且没有显示配置配置中心的情况下，Dubbo 框架会默认将此 Zookeeper 用作配置中心，但将只作服务治理用途
```
```xml
<dubbo:config-center address="zookeeper://127.0.0.1:2181"/>
 <!-- 集群配置：
    	address="10.20.153.10:2181,10.20.153.11:2181,10.20.153.12:2181"
    	address="zookeeper://10.20.153.10:2181?backup=10.20.153.11:2181,10.20.153.12:2181"
         -->
```

默认节点结构：

![image-20230223171833430](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223171833430.png)

    namespace，用于不同配置的环境隔离。
    config，Dubbo 约定的固定节点，不可更改，所有配置和服务治理规则都存储在此节点下。
    dubbo，所有服务治理规则都是全局性的，dubbo 为默认节点
    configurators/tag-router/condition-router，不同的服务治理规则类型，node value 存储具体规则内容
## 注册中心

### Zookeeper 注册中心

Zookeeper 是 Apache Hadoop 的子项目，是一个树型的目录服务，支持变更推送，适合作为 Dubbo 服务的注册中心，工业强度较高，可用于生产环境，并推荐使用 1。

![image-20230223173117024](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223173117024.png)

流程说明：

    服务提供者启动时: 向 /dubbo/com.foo.BarService/providers 目录下写入自己的 URL 地址
    服务消费者启动时: 订阅 /dubbo/com.foo.BarService/providers 目录下的提供者 URL 地址。并向 /dubbo/com.foo.BarService/consumers 目录下写入自己的 URL 地址
    监控中心启动时: 订阅 /dubbo/com.foo.BarService 目录下的所有提供者和消费者 URL 地址。

支持以下功能：

    当提供者出现断电等异常停机时，注册中心能自动删除提供者信息
    当注册中心重启时，能自动恢复注册数据，以及订阅请求
    当会话过期时，能自动恢复注册数据，以及订阅请求
    当设置 <dubbo:registry check="false" /> 时，记录失败注册和订阅请求，后台定时重试
    可通过 <dubbo:registry username="admin" password="1234" /> 设置 zookeeper 登录信息
    可通过 <dubbo:registry group="dubbo" /> 设置 zookeeper 的根节点，不配置将使用默认的根节点。
    支持 * 号通配符 <dubbo:reference group="*" version="*" />，可订阅服务的所有分组和所有版本的提供者
使用

在 provider 和 consumer 中增加 zookeeper 客户端 jar 包依赖：

```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.8.0</version>
</dependency>
```

## dubbo 基于Multicast的注册中心示例

Multicast 读法 [ˈmʌltikɑ:st]

### pom.xml引入依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo</artifactId>
    <version>2.6.9</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.32.Final</version>
</dependency>
```



### 服务端-服务提供者

#### alibaba-dubbo-learn-multicast-xml-server-example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="alibaba-dubbo-learn-multicast-xml-server-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露服务地址  N/A 代表不需要注册中心     multicast://224.5.6.7:1234
    	组播受网络结构限制，只适合小规模应用或开发阶段使用。组播地址段: 224.0.0.0 - 239.255.255.255配置
    -->
    <dubbo:registry address="multicast://224.5.6.7:1234" />
 
    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="com.julong.dubbo.service.HelloService" ref="helloService" />
 
    <!-- 和本地bean一样实现服务 -->
    <bean id="helloService" class="com.julong.dubbo.service.impl.HelloServiceImpl" />
</beans>

```

启动日志：

```log
二月 23, 2023 5:50:52 下午 org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
信息: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@50d4855d: startup date [Thu Feb 23 17:50:52 CST 2023]; root of context hierarchy
二月 23, 2023 5:50:52 下午 org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
信息: Loading XML bean definitions from class path resource [dubbo-server.xml]
二月 23, 2023 5:50:53 下午 com.alibaba.dubbo.common.logger.LoggerFactory info
信息: using logger: com.alibaba.dubbo.common.logger.jcl.JclLoggerAdapter
二月 23, 2023 5:50:53 下午 com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory warn
警告:  [DUBBO] No spring extension (bean) named:defaultCompiler, try to find an extension (bean) of type java.lang.String, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:53 下午 com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory warn
警告:  [DUBBO] No spring extension (bean) named:defaultCompiler, type:java.lang.String found, stop get bean., dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:53 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] The service ready on spring started. service: com.julong.dubbo.service.HelloService, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] Export dubbo service com.julong.dubbo.service.HelloService to local registry, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] Export dubbo service com.julong.dubbo.service.HelloService to url dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&bind.ip=192.168.10.128&bind.port=20880&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] Register dubbo service com.julong.dubbo.service.HelloService url dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&bind.ip=192.168.10.128&bind.port=20880&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933 to registry registry://224.5.6.7:1234/com.alibaba.dubbo.registry.RegistryService?application=alibaba-dubbo-learn-multicast-xml-server-example&dubbo=2.0.2&owner=julong&pid=5832&registry=multicast&timestamp=1677145853920, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.qos.server.Server info
信息:  [DUBBO] qos-server bind localhost:22222, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.remoting.transport.AbstractServer info
信息:  [DUBBO] Start NettyServer bind /0.0.0.0:20880, export /192.168.10.128:20880, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Register: dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Send broadcast message: register dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933 to /224.5.6.7:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Receive multicast message: register dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933 from /127.0.0.1:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Subscribe: provider://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&category=configurators&check=false&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Send broadcast message: subscribe provider://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&category=configurators&check=false&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933 to /224.5.6.7:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:54 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Receive multicast message: subscribe provider://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&category=configurators&check=false&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933 from /127.0.0.1:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:50:55 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry warn
警告:  [DUBBO] Ignore empty notify urls for subscribe url provider://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&category=configurators&check=false&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933, dubbo version: 2.6.9, current host: 192.168.10.128
服务启动

```



### 服务端-服务消费者

#### alibaba-dubbo-learn-multicast-xml-client-example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="alibaba-dubbo-learn-multicast-xml-client-example" owner="julong" >
   		 <!-- 同一服务器上 需要设置 如下配置  消费者同样需要声明unicast=false，否则消费者无法收到消息，导致No provider available for the service异常：-->
    	<dubbo:parameter key="qos.enable" value="true" />
		<dubbo:parameter key="qos.accept.foreign.ip" value="false" />
		<dubbo:parameter key="qos.port" value="33333" />
		<dubbo:parameter key="unicast" value="false" />
    </dubbo:application>

    <!-- 使用multicast广播注册中心暴露发现服务地址 multicast://224.5.6.7:1234-->
    <dubbo:registry address="multicast://224.5.6.7:1234" />
    
 	 <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 生成远程服务代理，可以和本地bean一样使用demoService  无注册中心的方法 使用url 配置
    check="false" 可以通过 check="false" 关闭检查，比如，测试时，有些服务不关心，或者出现了循环依赖，必须有一方先启动。
    retries 服务在尝试调用一次之后，如出现非业务异常(服务突然不可用、超时等)，Dubbo 默认会进行额外的最多2次重试.
    -->
    <dubbo:reference id="helloService" interface="com.julong.dubbo.service.HelloService" retries="2" check="false" />
</beans>

```



服务启动日志：

```txt
二月 23, 2023 5:51:29 下午 org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
信息: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@5d1e8050: startup date [Thu Feb 23 17:51:29 CST 2023]; root of context hierarchy
二月 23, 2023 5:51:29 下午 org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
信息: Loading XML bean definitions from class path resource [dubbo-client.xml]
二月 23, 2023 5:51:29 下午 com.alibaba.dubbo.common.logger.LoggerFactory info
信息: using logger: com.alibaba.dubbo.common.logger.jcl.JclLoggerAdapter
二月 23, 2023 5:51:29 下午 com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory warn
警告:  [DUBBO] No spring extension (bean) named:defaultCompiler, try to find an extension (bean) of type java.lang.String, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:29 下午 com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory warn
警告:  [DUBBO] No spring extension (bean) named:defaultCompiler, type:java.lang.String found, stop get bean., dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.qos.server.Server info
信息:  [DUBBO] qos-server bind localhost:33333, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Register: consumer://192.168.10.128/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-multicast-xml-client-example&category=consumers&check=false&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&retries=2&side=consumer&timestamp=1677145890219&unicast=false, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Send broadcast message: register consumer://192.168.10.128/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-multicast-xml-client-example&category=consumers&check=false&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&retries=2&side=consumer&timestamp=1677145890219&unicast=false to /224.5.6.7:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Receive multicast message: register consumer://192.168.10.128/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-multicast-xml-client-example&category=consumers&check=false&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&retries=2&side=consumer&timestamp=1677145890219&unicast=false from /127.0.0.1:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Subscribe: consumer://192.168.10.128/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-multicast-xml-client-example&category=providers,configurators,routers&check=false&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&retries=2&side=consumer&timestamp=1677145890219&unicast=false, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Send broadcast message: subscribe consumer://192.168.10.128/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-multicast-xml-client-example&category=providers,configurators,routers&check=false&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&retries=2&side=consumer&timestamp=1677145890219&unicast=false to /224.5.6.7:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Receive multicast message: subscribe consumer://192.168.10.128/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-multicast-xml-client-example&category=providers,configurators,routers&check=false&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&retries=2&side=consumer&timestamp=1677145890219&unicast=false from /127.0.0.1:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Receive multicast message: register dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933 from /127.0.0.1:1234, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Notify urls for subscribe url consumer://192.168.10.128/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-multicast-xml-client-example&category=providers,configurators,routers&check=false&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&retries=2&side=consumer&timestamp=1677145890219&unicast=false, urls: [dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933], dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.remoting.transport.AbstractClient info
信息:  [DUBBO] Successed connect to server /192.168.10.128:20880 from NettyClient 192.168.10.128 using dubbo version 2.6.9, channel is NettyChannel [channel=[id: 0xf676ff04, L:/192.168.10.128:54618 - R:/192.168.10.128:20880]], dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:30 下午 com.alibaba.dubbo.remoting.transport.AbstractClient info
信息:  [DUBBO] Start NettyClient WIN-GMBJ6AGDUPM/192.168.10.128 connect to the server /192.168.10.128:20880, dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:31 下午 com.alibaba.dubbo.registry.multicast.MulticastRegistry info
信息:  [DUBBO] Notify urls for subscribe url consumer://192.168.10.128/com.julong.dubbo.service.HelloService?application=alibaba-dubbo-learn-multicast-xml-client-example&category=providers,configurators,routers&check=false&dubbo=2.0.2&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&retries=2&side=consumer&timestamp=1677145890219&unicast=false, urls: [dubbo://192.168.10.128:20880/com.julong.dubbo.service.HelloService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-server-example&bean.name=com.julong.dubbo.service.HelloService&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=5832&side=provider&timestamp=1677145853933], dubbo version: 2.6.9, current host: 192.168.10.128
二月 23, 2023 5:51:31 下午 com.alibaba.dubbo.config.AbstractConfig info
信息:  [DUBBO] Refer dubbo service com.julong.dubbo.service.HelloService from url multicast://224.5.6.7:1234/com.alibaba.dubbo.registry.RegistryService?anyhost=true&application=alibaba-dubbo-learn-multicast-xml-client-example&bean.name=com.julong.dubbo.service.HelloService&check=false&dubbo=2.0.2&generic=false&interface=com.julong.dubbo.service.HelloService&methods=sayHello&owner=julong&pid=6112&qos.accept.foreign.ip=false&qos.enable=true&qos.port=33333&register.ip=192.168.10.128&remote.timestamp=1677145853933&retries=2&side=consumer&timestamp=1677145890219&unicast=false, dubbo version: 2.6.9, current host: 192.168.10.128
dubbo :hello world
二月 23, 2023 5:51:31 下午 com.alibaba.dubbo.config.DubboShutdownHook info
信息:  [DUBBO] Run shutdown hook now., dubbo version: 2.6.9, current host: 192.168.10.128

```



### Multicast 注册中心

Multicast 注册中心不需要启动任何中心节点，只要广播地址一样，就可以互相发现。

![image-20230223174625175](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223174625175.png)

    0.提供方启动时广播自己的地址
    1.消费方启动时广播订阅请求
    2.提供方收到订阅请求时，单播自己的地址给订阅者，如果设置了 unicast=false，则广播给订阅者
    3.消费方收到提供方地址时，连接该地址进行 RPC 调用。

组播受网络结构限制，只适合小规模应用或开发阶段使用。组播地址段: 224.0.0.0 - 239.255.255.255

配置

```xml
<dubbo:registry address="multicast://224.5.6.7:1234" />
```

或

```xml
<dubbo:registry protocol="multicast" address="224.5.6.7:1234" />
```

为了减少广播量，Dubbo 缺省使用单播发送提供者地址信息给消费者，如果一个机器上同时启了多个消费者进程，消费者需声明 unicast=false，否则只会有一个消费者能收到消息; 当服务者和消费者运行在同一台机器上，消费者同样需要声明unicast=false，否则消费者无法收到消息，导致No provider available for the service异常：

```xml
<dubbo:application name="demo-consumer">
    <dubbo:parameter key="unicast" value="false" />
</dubbo:application>
```

或

```xml
<dubbo:consumer>
    <dubbo:parameter key="unicast" value="false" />
</dubbo:consumer>
```



## dubbo 基于redis的注册中心示例

### pom.xml引入依赖配置

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo-registry-redis</artifactId>
    <version>2.6.5</version>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo</artifactId>
    <version>2.6.9</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.32.Final</version>
</dependency>
```



### 服务端-服务提供者

#### alibaba-dubbo-learn-redis-xml-server-example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="alibaba-dubbo-learn-redis-xml-server-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露服务地址  N/A 代表不需要注册中心     multicast://224.5.6.7:1234
    redis 无用户名  username="julong"  此处可以设置登录名  名字随便设置 都可以 
    -->
    <dubbo:registry address="redis://192.168.10.222:6379" username="julong" password="123456" cluster="failover"/>
 
    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="com.julong.dubbo.service.HelloService" ref="helloService" />
 
    <!-- 和本地bean一样实现服务 -->
    <bean id="helloService" class="com.julong.dubbo.service.impl.HelloServiceImpl" />
</beans>

```



### 服务端-服务消费者

#### alibaba-dubbo-learn-redis-xml-client-example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 

 
    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="alibaba-dubbo-learn-redis-xml-client-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露发现服务地址 multicast://224.5.6.7:1234-->
    <dubbo:registry address="redis://192.168.10.222:6379" username="julong" password="123456" />
    
 	 <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 
           生成远程服务代理，可以和本地bean一样使用demoService  无注册中心的方法 使用url 配置
            生成远程服务代理，dubbo:reference 可以和本地bean一样使用helloService 
   	check="false" 忽略检查   服务提供者不存在 则不报错
   	check="true" 检查  服务提供者 如果不存在则报错
   	timeout 服务方法调用超时时间(毫秒)
    retries	远程服务调用重试次数，不包括第一次调用，不需要重试请设为0   新增不要设置 重试次数
    version 服务版本，与服务提供者的版本一致   *代表 随机
    -->
    <dubbo:reference id="helloService" interface="com.julong.dubbo.service.HelloService" />
</beans>

```

服务启动错误排查：

```java
Caused by: redis.clients.jedis.exceptions.JedisDataException: ERR Client sent AUTH, but no password is set
	at redis.clients.jedis.Protocol.processError(Protocol.java:127)
	at redis.clients.jedis.Protocol.process(Protocol.java:161)
	at redis.clients.jedis.Protocol.read(Protocol.java:215)
	at redis.clients.jedis.Connection.readProtocolWithCheckingBroken(Connection.java:340)
	at redis.clients.jedis.Connection.getStatusCodeReply(Connection.java:239)
	at redis.clients.jedis.BinaryJedis.auth(BinaryJedis.java:2139)
	at redis.clients.jedis.JedisFactory.makeObject(JedisFactory.java:108)
	at org.apache.commons.pool2.impl.GenericObjectPool.create(GenericObjectPool.java:868)
	at org.apache.commons.pool2.impl.GenericObjectPool.borrowObject(GenericObjectPool.java:435)
	at org.apache.commons.pool2.impl.GenericObjectPool.borrowObject(GenericObjectPool.java:363)
	at redis.clients.util.Pool.getResource(Pool.java:49)
	... 26 more
```

此错误为redis 没有设置密码 而服务配置则设置了访问密码 需要修改redis启动方式

```shell
[julong@localhost redis-3.0.0]$ ./redis-server redis.conf &
```



### Redis 注册中心

Redis 过期数据通过心跳的方式检测脏数据，服务器时间必须同步，并且对服务器有一定压力，否则过期检测会不准确

![image-20230223181210115](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223181210115.png)

使用 Redis 的 Key/Map 结构存储数据结构：

```
主 Key 为服务名和类型
Map 中的 Key 为 URL 地址
Map 中的 Value 为过期时间，用于判断脏数据，脏数据由监控中心删除 [^3]
```

使用 Redis 的 Publish/Subscribe 事件通知数据变更：

    通过事件的值区分事件类型：register, unregister, subscribe, unsubscribe
    普通消费者直接订阅指定服务提供者的 Key，只会收到指定服务的 register, unregister 事件
    监控中心通过 psubscribe 功能订阅 /dubbo/*，会收到所有服务的所有变更事件

调用过程：

    0.服务提供方启动时，向 Key:/dubbo/com.foo.BarService/providers 下，添加当前提供者的地址
    1.并向 Channel:/dubbo/com.foo.BarService/providers 发送 register 事件
    2.服务消费方启动时，从 Channel:/dubbo/com.foo.BarService/providers 订阅 register 和 unregister 事件
    3.并向 Key:/dubbo/com.foo.BarService/consumers 下，添加当前消费者的地址
    4.服务消费方收到 register 和 unregister 事件后，从 Key:/dubbo/com.foo.BarService/providers 下获取提供者地址列表
    5.服务监控中心启动时，从 Channel:/dubbo/* 订阅 register 和 unregister，以及 subscribe 和unsubsribe 事件
    6.服务监控中心收到 register 和 unregister 事件后，从 Key:/dubbo/com.foo.BarService/providers 下获取提供者地址列表
    7.服务监控中心收到 subscribe 和 unsubsribe 事件后，从 Key:/dubbo/com.foo.BarService/consumers 下获取消费者地址列表
配置

```xml
<dubbo:registry address="redis://10.20.153.10:6379" />
```

或

```xml
<dubbo:registry address="redis://10.20.153.10:6379?backup=10.20.153.11:6379,10.20.153.12:6379" />
```

或

```xml
<dubbo:registry protocol="redis" address="10.20.153.10:6379" />
```

或

```xml
<dubbo:registry protocol="redis" address="10.20.153.10:6379,10.20.153.11:6379,10.20.153.12:6379" />
```

选项

    可通过 <dubbo:registry group="dubbo" /> 设置 redis 中 key 的前缀，缺省为 dubbo。
    可通过 <dubbo:registry cluster="replicate" /> 设置 redis 集群策略，缺省为 failover：
        failover: 只写入和读取任意一台，失败时重试另一台，需要服务器端自行配置数据同步
        replicate: 在客户端同时写入所有服务器，只读取单台，服务器端不需要同步，注册中心集群增大，性能压力也会更大

可靠性声明

阿里内部并没有采用 Redis 做为注册中心，而是使用自己实现的基于数据库的注册中心，即：Redis 注册中心并没有在阿里内部长时间运行的可靠性保障，此 Redis 桥接实现只为开源版本提供，其可靠性依赖于 Redis 本身的可靠性。

## dubbo 基于nacos的注册中心示例

### pom.xml引入依赖

```xml
<!-- Dubbo Nacos registry dependency -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>dubbo-registry-nacos</artifactId>
			<version>0.0.2</version>
		</dependency>

		<!-- Keep latest Nacos client version -->
		<dependency>
	        <groupId>com.alibaba.nacos</groupId>
	        <artifactId>nacos-client</artifactId>
	        <version>0.6.1</version>
	    </dependency>

		<!-- Dubbo dependency -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>dubbo</artifactId>
			<version>2.6.5</version>
		</dependency>

		<!-- Alibaba Spring Context extension -->
		<dependency>
			<groupId>com.alibaba.spring</groupId>
			<artifactId>spring-context-support</artifactId>
			<version>1.0.2</version>
		</dependency>

		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-core</artifactId>
			<version>2.8.1</version>
		</dependency>
```



### 服务端-服务提供者

#### alibaba-dubbo-learn-nacos-xml-server-example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="alibaba-dubbo-learn-nacos-xml-server-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露服务地址  N/A 代表不需要注册中心     multicast://224.5.6.7:1234-->
    <dubbo:registry address="nacos://192.168.10.222:8848" />
 
    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="com.julong.dubbo.service.HelloService" ref="helloService" />
 
    <!-- 和本地bean一样实现服务 -->
    <bean id="helloService" class="com.julong.dubbo.service.impl.HelloServiceImpl" />
</beans>

```



### 服务端-服务消费者

#### alibaba-dubbo-learn-nacos-xml-client-example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 

 
    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="alibaba-dubbo-learn-nacos-xml-client-example" owner="julong" />
 
    <!-- 使用nacos注册中心暴露发现服务地址 -->
    <dubbo:registry address="nacos://192.168.10.222:8848" />
    
 	 <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 
           生成远程服务代理，可以和本地bean一样使用demoService  无注册中心的方法 使用url 配置
            生成远程服务代理，dubbo:reference 可以和本地bean一样使用helloService 
   	check="false" 忽略检查   服务提供者不存在 则不报错
   	check="true" 检查  服务提供者 如果不存在则报错
   	timeout 服务方法调用超时时间(毫秒)
    retries	远程服务调用重试次数，不包括第一次调用，不需要重试请设为0   新增不要设置 重试次数
    version 服务版本，与服务提供者的版本一致   *代表 随机
    -->
    <dubbo:reference id="helloService" interface="com.julong.dubbo.service.HelloService"  />
</beans>

```





### Nacos 注册中心

Nacos 是 Dubbo 生态系统中重要的注册中心实现，其中 dubbo-registry-nacos 则是 Dubbo 融合 Nacos 注册中心的实现。建议使用 Nacos 1.0.0 及以上的版本。

#### Nacos安装

下载安装包然后解压   tar -xvf nacos-server-$version.tar.gz

##### 创建数据库

找到 conf目录下的 nacos-mysql.sql 文件

然后创建 数据库全名 = nacos_config  数据库

执行sql脚本 此步骤必须做

![image-20230223204200980](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223204200980.png)

linux 启动命令：sh startup.sh -m standalone

windows启动命令：startup.cmd -m standalone

#### 服务注册&发现和配置管理

##### 服务注册

curl -X POST 'http://127.0.0.1:8848/nacos/v1/ns/instance?serviceName=nacos.naming.serviceName&ip=20.18.7.10&port=8080'

##### 服务发现

curl -X GET 'http://127.0.0.1:8848/nacos/v1/ns/instance/list?serviceName=nacos.naming.serviceName'

##### 发布配置

curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test&content=HelloWorld"

##### 获取配置

curl -X GET "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test"

nacos配置文件需要修改的配置：

```properties
#*************** Config Module Related Configurations ***************#
### If use MySQL as datasource:
spring.datasource.platform=mysql

### Count of DB:
db.num=1

### Connect URL of DB:
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user=root
db.password=root
```



服务查看页面

![image-20230223210943106](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230223210943106.png)



## dubbo 基于springboot注解示例

### 版本依赖

| Dubbo Spring Boot | Dubbo  | Spring Boot |
| ----------------- | ------ | ----------- |
| 0.2.1.RELEASE     | 2.6.5+ | 2.x         |
| 0.1.2.RELEASE     | 2.6.5+ | 1.x         |

服务目录设计结构

### alibaba-dubbo-learn-annotation-example 

-- alibaba-dubbo-learn-annotation-api-example	 共享api模块
-- alibaba-dubbo-learn-annotation-consumer-example 	消费端模块
-- alibaba-dubbo-learn-annotation-provider-example	 服务端模块

#### alibaba-dubbo-learn-annotation-api-example 

共享api模块，此模块为公用类和接口

![image-20230224195658157](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230224195658157.png)

```java
package com.julong.service;

import com.julong.entity.UserInfo;

/**
 * 测试接口
 * @author julong
 * @date 2020年2月24日 下午7:10:50
 * @desc 
 */
public interface HelloService {

	/**
	 * 测试方法
	 * @param userInfo
	 * @return
	 * @throws Exception
	 * @author julong
	 * @date 2020年2月24日 下午7:10:56
	 * @desc
	 */
	public abstract String sayHello(UserInfo userInfo) throws Exception;
}

```

UserInfo.java

```java
package com.julong.entity;

import java.io.Serializable;

/**
 * 用户信息
 * @author julong
 * @date 2020年2月24日 下午7:09:23
 * @desc 
 */
public class UserInfo implements Serializable{

	/**
	 * @author julong
	 * @date 2020年2月24日 下午11:32:48
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 用户名称
	 * @author julong
	 * @date 2020年2月24日 下午7:09:30
	 */
	private String userName;

	/**
	 * @return String the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}

```

#### alibaba-dubbo-learn-annotation-provider-example

服务端模块

![image-20230224195838423](D:\Workspaces\OWER\alibaba-dubbo-learn-examples\images\image-20230224195838423.png)

##### pom文件引入依赖

```xml
<dependencies>
		<dependency>
			<groupId>com.julong</groupId>
			<artifactId>alibaba-dubbo-learn-annotation-api-example</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<optional>true</optional>
		</dependency>
			<!-- 针对 spring-boot 1.x -->
	<!-- 	<dependency>
			<groupId>com.alibaba.boot</groupId>
			<artifactId>dubbo-spring-boot-starter</artifactId>
			<version>0.1.2.RELEASE</version>
		</dependency> -->
		<!-- 版本不一样会报错的 使用 0.1.2 的时候 applicationConfig 注解版 会报错 -->
        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>0.1.1</version>
        </dependency>
</dependencies>
```

##### ProviderDubboConfig.java dubbo配置

```java
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

```

##### HelloServiceImpl.java 接口实现

注意 @Service 为  dubbo 的 注解 而非spring的注解 com.alibaba.dubbo.config.annotation.Service

```java
/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.julong.service.impl;


import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.julong.entity.UserInfo;
import com.julong.service.HelloService;

/**
 * 测试接口实现类
 * @author julong
 * @date 2020年2月24日 下午8:11:13
 * @desc 
 */
@Service
@Component
public class HelloServiceImpl implements HelloService{

	@Override
	public String sayHello(UserInfo userInfo) throws Exception {
		// TODO Auto-generated method stub
		return "你好！" + userInfo.getUserName();
	}


}

```

##### DubboProviderApplication.java 启动类

其中 @EnableDubbo(scanBasePackages="com.julong")  为dubbo注解 和配置扫描的包

```java
/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.julong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@EnableDubbo(scanBasePackages="com.julong") //启用 dubbo注解
@SpringBootApplication
public class DubboProviderApplication {


	public static void main(String[] args) throws Exception {
		SpringApplication.run(DubboProviderApplication.class, args);
	}
}

```

#### alibaba-dubbo-learn-annotation-consumer-example

服务调用方

ConsumerDubboConfig.java

```java
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
		registryConfig.setAddress("192.168.10.222:2181");
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

```



## dubbo基于xml配置文件配置文件解析原理

### BeanDefinitionParser

解析xml 调用 spring 中 org.springframework.beans.factory.xml.BeanDefinitionParser.class

通过实现此接口来进行xml 解析

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
    http://dubbo.apache.org/schema/dubbo        
    http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="alibaba-dubbo-learn-xml-server-example" owner="julong" />
 
    <!-- 使用multicast广播注册中心暴露服务地址  N/A 代表不需要注册中心     multicast://224.5.6.7:1234-->
    <dubbo:registry address="N/A" />
 
    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="com.julong.dubbo.service.HelloService" ref="helloService" />
 
    <!-- 和本地bean一样实现服务 -->
    <bean id="helloService" class="com.julong.dubbo.service.impl.HelloServiceImpl" />
</beans>

```

按照书写顺序进行解析

### DubboBeanDefinitionParser

com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser.class中 parse方法

```java
/**
 * AbstractBeanDefinitionParser
 *
 * @export
 */
public class DubboBeanDefinitionParser implements BeanDefinitionParser {

    private static final Logger logger = LoggerFactory.getLogger(DubboBeanDefinitionParser.class);
    private static final Pattern GROUP_AND_VERION = Pattern.compile("^[\\-.0-9_a-zA-Z]+(\\:[\\-.0-9_a-zA-Z]+)?$");
    private final Class<?> beanClass;
    private final boolean required;

    public DubboBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    // 此方法 解析 xml 并且解析xml 中的 节点xml 
    @SuppressWarnings("unchecked")
    private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
    ......
	}
    ......
}
```

### DubboNamespaceHandler

```java
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.config.spring.schema;

import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * DubboNamespaceHandler
 * 命名空间
 * @export
 */
public class DubboNamespaceHandler extends NamespaceHandlerSupport {

    static {
        Version.checkDuplicate(DubboNamespaceHandler.class);
    }
	//初始化对应的标签
    @Override
    public void init() {
        registerBeanDefinitionParser("application", new DubboBeanDefinitionParser(ApplicationConfig.class, true));
        registerBeanDefinitionParser("module", new DubboBeanDefinitionParser(ModuleConfig.class, true));
        registerBeanDefinitionParser("registry", new DubboBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("monitor", new DubboBeanDefinitionParser(MonitorConfig.class, true));
        registerBeanDefinitionParser("provider", new DubboBeanDefinitionParser(ProviderConfig.class, true));
        registerBeanDefinitionParser("consumer", new DubboBeanDefinitionParser(ConsumerConfig.class, true));
        registerBeanDefinitionParser("protocol", new DubboBeanDefinitionParser(ProtocolConfig.class, true));
        registerBeanDefinitionParser("service", new DubboBeanDefinitionParser(ServiceBean.class, true));
        registerBeanDefinitionParser("reference", new DubboBeanDefinitionParser(ReferenceBean.class, false));
        registerBeanDefinitionParser("annotation", new AnnotationBeanDefinitionParser());
    }

}

```



