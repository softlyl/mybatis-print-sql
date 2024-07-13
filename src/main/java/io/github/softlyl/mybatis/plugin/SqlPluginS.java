package io.github.softlyl.mybatis.plugin;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class})
})
@Component
public class SqlPluginS implements Interceptor {

    @Value("${mybatis.printSql.logLevel:debug}")
    private String logLevel;

    private static final Logger log=LoggerFactory.getLogger(SqlPluginS.class);
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        final Object[] args = invocation.getArgs();
        Connection connection = (Connection) args[0];
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Configuration configuration = mappedStatement.getConfiguration();
        //Object parameter= boundSql.getParameterObject();

        long start = System.currentTimeMillis();
        Object result=null;
        try {
            result=invocation.proceed();
        } finally {
            long end = System.currentTimeMillis();
            if(logLevel.equalsIgnoreCase("debug")){
                log.debug("sql执行时间为:"+ (end-start));
            }else if(logLevel.equalsIgnoreCase("info")){
                log.info("sql执行时间为:"+ (end-start));
            }else if(logLevel.equalsIgnoreCase("warn")){
                log.warn("sql执行时间为:"+ (end-start));
            }
            //System.out.println("执行时间为 = " + (end-start));
            String sql=getSql(configuration,boundSql,connection);
            if(logLevel.equalsIgnoreCase("debug")){
                log.debug("执行的sql为:"+ sql);
            }else if(logLevel.equalsIgnoreCase("info")){
                log.info("执行的sql为:"+ sql);
            }else if(logLevel.equalsIgnoreCase("warn")){
                log.warn("执行的sql为:"+ sql);
            }
            //System.out.println("执行的sql = " + sql);
        }

        return result;
    }

    private String getSql(Configuration configuration, BoundSql boundSql,Connection connection) throws SQLException {
        String sql = boundSql.getSql();
        if(!StringUtils.hasText(sql)){
            return  "";
        }
        sql=easyReadSql(sql);
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        if(!CollectionUtils.isEmpty(parameterMappings) && parameterObject!=null){
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            //是否有对应的类型处理器 基本类型
            if(typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())){
                sql=replaceSql(sql,parameterObject,connection);
            }else {//对象
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for(ParameterMapping paraMap:parameterMappings){
                    String propertyName = paraMap.getProperty();
                    if(metaObject.hasGetter(propertyName)){
                        Object obj = metaObject.getValue(propertyName);
                        sql=replaceSql(sql,obj,connection);
                    }else if(boundSql.hasAdditionalParameter(propertyName)){//判断是否有动态参数
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql=replaceSql(sql,obj,connection);
                    }

                }
            }

        }
        return sql;
    }

    private String replaceSql(String sql, Object obj,Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        //获取数据库产品名
        String databaseProductName = metaData.getDatabaseProductName();
        String value="";
        if(obj instanceof String){
            value="'"+obj.toString()+"'";
        }else if(obj instanceof Date){
           //根据数据库类型来转
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime = ((Date) obj).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            //是否是oralce数据库
            if(databaseProductName.toLowerCase().contains("oracle")) {
                value = "to_date('" + localDateTime.format(formatter) + "','YYYY-MM-DD HH24:MI:SS')";
            }else{
                //默认mysql
                value="str_to_date('" + localDateTime.format(formatter)+ "','%Y-%m-%d %H:%i:%s')";
            }
        }else if (obj instanceof LocalDateTime) {//暂不根据数据库类型处理 还没见过谁用
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            value = "'" + ((LocalDateTime) obj).format(formatter) + "'";
        } else if (obj instanceof BigDecimal) {
            //value = ((BigDecimal) obj).stripTrailingZeros().toPlainString();
            //为了所见及所得，不去除后边的0
            value = ((BigDecimal) obj).toPlainString();
        } else if (obj instanceof Boolean) {
            if (((Boolean) obj)) {
                value = "1";
            } else {
                value = "0";
            }
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                //null的时候，DB执行时就行null,占个位即可
                value = "null";
            }
        }
        return sql.replaceFirst("\\?",value);
    }

    private String easyReadSql(String sql) {
        //多个空格 替换成一个
        return sql.replaceAll("[\\s\n]+"," ");
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
