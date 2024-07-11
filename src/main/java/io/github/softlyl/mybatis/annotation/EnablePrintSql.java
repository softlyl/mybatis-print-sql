package io.github.softlyl.mybatis.annotation;

import io.github.softlyl.mybatis.PrintSqlAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({PrintSqlAutoConfig.class})
public @interface EnablePrintSql {
}
