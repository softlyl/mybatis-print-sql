package io.github.softlyl.mybatis.plugin;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.objenesis.instantiator.basic.FailingInstantiator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Properties;
@Intercepts({@Signature(type= Executor.class,method = "query",args ={MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
            @Signature(type= Executor.class,method = "query",args ={MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class}),
            @Signature(type= Executor.class,method = "update",args ={MappedStatement.class,Object.class})})
@Component
public class SqlPlugin implements Interceptor {

    private static final Logger log=LoggerFactory.getLogger(SqlPlugin.class);
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        Object parameter= invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();

        long start = System.currentTimeMillis();
        Object result=null;
        try {
            result=invocation.proceed();
        } finally {
            long end = System.currentTimeMillis();
            System.out.println("执行时间为 = " + (end-start));
            String sql=getSql(configuration,boundSql);
            System.out.println("执行的sql = " + sql);
        }

        return result;
    }

    private String getSql(Configuration configuration, BoundSql boundSql) {
        String sql = boundSql.getSql();
        if(StringUtils.hasText(sql)){
            return  "";
        }
        sql=easyReadSql(sql);
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        if(!CollectionUtils.isEmpty(parameterMappings) && parameterObject!=null){
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            //是否有对应的类型处理器 基本类型
            if(typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())){
                sql=replaceSql(sql,parameterObject);
            }else {//对象
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for(ParameterMapping paraMap:parameterMappings){
                    String propertyName = paraMap.getProperty();
                    if(metaObject.hasGetter(propertyName)){
                        Object obj = metaObject.getValue(propertyName);
                        replaceSql(sql,obj);
                    }else if(boundSql.hasAdditionalParameter(propertyName)){//判断是否有动态参数
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql=replaceSql(sql,obj);
                    }

                }
            }

        }
        return sql;
    }

    private String replaceSql(String sql, Object parameterObject) {
        String ret="";
        if(parameterObject instanceof String){
            ret="'"+parameterObject.toString()+"'";
        }else if(parameterObject instanceof Date){
           //根据数据库类型来转
            //ret=
        }else{
            ret=parameterObject.toString();
        }
        return sql.replaceFirst("\\?",ret);
    }

    private String easyReadSql(String sql) {
        //多个空格 替换成一个
        return sql.replaceAll("[\\s\n]+","");
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
