package dev.markodojkic.base.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration.class)
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean(DataSource.class)
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "app.datasource.pool", havingValue = "c3p0")
    @ConfigurationProperties("spring.datasource")
    DataSource c3p0DataSource() {
        return DataSourceBuilder.create()
                .type(ComboPooledDataSource.class)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.datasource.pool", havingValue = "hikari", matchIfMissing = true)
    @ConfigurationProperties("spring.datasource.hikari")
    DataSource hikariDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }
}
