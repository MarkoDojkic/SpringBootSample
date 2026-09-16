package dev.markodojkic.config;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

class DataSourceConfigTest {
    @Test
    void shouldProvideC3p0DataSource() {
        assertInstanceOf(ComboPooledDataSource.class, new DataSourceConfig().c3p0DataSource());
    }

    @Test
    void shouldProvideHikariDataSource() {
        assertInstanceOf(HikariDataSource.class, new DataSourceConfig().hikariDataSource());
    }
}
