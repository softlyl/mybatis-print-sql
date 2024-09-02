> 项目简介 

集成此项目可以打印出集成任何使用slf4j门面模式的日志框架的sql语句,目前支持mysql和oracle, 可以打印spring集成mybatis框架的sql语句，而且里边的问号会自动放到对应的位置 拷贝出来就可以直接执行 提高开发及调试效率

# 使用步骤

1. 引入maven坐标 
   
   ```java
           <dependency>
               <groupId>io.github.softlyl</groupId>
               <artifactId>mybatis-print-sql</artifactId>
               <version>1.0.5</version>
           </dependency>
   ```

2. 在springboot启动类上加上@EnablePrintSql这个注解
   
   如下图![](https://raw.githubusercontent.com/softlyl/java-Concurrent/master/2024-07-14-15-31-46-image.png)

3. 默认打印sql的日志级别是输出到debug文件中，不需要配置，如需把sql语句输入到info文件，则更改日志级别为info 需在配置文件中加入mybatis.printSql.logLevel=info，支持debug、info、error三种级别
   
   大家看效果图
   
   ![](https://raw.githubusercontent.com/softlyl/java-Concurrent/master/2024-07-14-15-39-49-image.png)
   
   # 版本更新说明：
   
   v1.0.4 
   
   更新内容：增加打印指定的mapper接口的sql语句

   v1.0.5 
   
   更新内容：当参数值中有？时，输出完整sql有误的问题
   
   配置项说明

| 配置项 | 说明  | 默认值 |
| --- | --- | --- |
| mybatis.printSql.logLevel | 完整sql语句打印哪个级别的日志文件中 | debug |
| mybatis.printSql.zdyMapperLocations | 只打印指定的mapper接口的sql,多个以英文逗号隔开，例如：com.dragon7531.bdemo.mapper.StudentMapper,   com.dragon7531.bdemo.mapper.StudentMapper2 | all |

# 注意事项：

如有自定义SqlSessionFactory的话，参照如下代码，不然不起作用

```java
@Configuration
@MapperScan(basePackages = {"com.dragon7531.bdemo.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {
    //加上此注入
    @Autowired
    private SqlPluginS SqlPlugin;
    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean ssfbean = new SqlSessionFactoryBean();
        //加上此行代码
        ssfbean.setPlugins(new Interceptor[]{SqlPlugin});
    }
}
```

# 配套视频教程地址
   
   https://www.bilibili.com/video/BV1Zr421T7pu
   
   https://www.bilibili.com/video/BV1cT421r7gU
   
   # 捐赠支持 你可以请作者喝杯咖啡表示鼓励
   
   关注微信公众号：程序龙 
   点击【java】自己开发的mybatis打印完整sql插件 这篇文章 滑到最后 点击喜欢作者按钮
