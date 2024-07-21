# mybatis-print-sql
> 项目简介

此包中可以打印出集成任何使用slf4j门面模式的日志框架的sql语句,目前支持mysql和oracle 在配置文件中加入mybatis.printSql.logLevel这个属性，值可以为info和warn,默认不加为debug,这样sql就可以打印到对应级别的日志文件中 引入此包 可以打印spring集成mybatis框架的sql语句，而且里边的问号会自动放到对应的位置 拷贝出来就可以直接执行，还打印出了sql的执行耗时，提高开发及调试效率

# 使用步骤

1. 引入maven坐标
  
  ```java
  		<dependency>
  			<groupId>io.github.softlyl</groupId>
  			<artifactId>mybatis-print-sql</artifactId>
  			<version>1.0.3</version>
  		</dependency>
  ```
  
2. 在springboot启动类上加上@EnablePrintSql这个注解
  
  ![](https://raw.githubusercontent.com/softlyl/java-Concurrent/master/2024-07-14-15-31-46-image.png)
  
3. 默认打印sql的日志级别是输出到debug文件中，不需要配置，如需把sql语句输入到info文件，则更改日志级别为info 需在配置文件中加入mybatis.printSql.logLevel=info，支持debug、info、error三种级别
  
  效果图
  
  ![](https://raw.githubusercontent.com/softlyl/java-Concurrent/master/2024-07-14-15-39-49-image.png)
  
# 配套视频教程地址
  https://www.bilibili.com/video/BV1Zr421T7pu/?vd_source=ae0e96843494a3941284f3830e9a8826
  
# 捐赠支持 你可以请作者喝杯咖啡表示鼓励

关注微信公众号：程序龙  点击【java】自己开发的mybatis打印完整sql插件 这篇文章 滑到最后 点击喜欢作者按钮
