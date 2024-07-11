package io.github.softlyl.mybatis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.github.softlyl.mybatis.plugin.SqlPluginS;

@Configuration
@Import({SqlPluginS.class})
public class PrintSqlAutoConfig {

}
