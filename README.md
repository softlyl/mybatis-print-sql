# mybatis-print-sql
此包中可以打印出集成任何使用slf4j门面模式的日志框架的sql语句,目前支持mysql和oracle,其他数据库产品打印sql时日期类型转换未匹配，有需要的小伙伴可以留言，我会抽出时间来匹配
在配置文件中加入mybatis.printSql.logLevel这个属性，值可以为info和warn,默认不加为debug,这样sql就可以打印到对应级别的日志文件中
引入此包 可以打印spring集成mybatis框架的sql语句，而且里边的问号会自动放到对应的位置 拷贝出来就可以直接执行 提高开发及调试效率
