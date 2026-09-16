package dev.markodojkic.base.datasource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class DataSourceAutoConfigurationTest {
    @Test
    void shouldBuildC3p0DataSource() {
        assertInstanceOf(ComboPooledDataSource.class, new DataSourceAutoConfiguration().c3p0DataSource());
    }

    @Test
    void shouldBuildHikariDataSource() {
        assertInstanceOf(HikariDataSource.class, new DataSourceAutoConfiguration().hikariDataSource());
    }

    @Test
    void shouldRegisterSpringBootAutoConfigurationMetadata() throws Exception {
        try (var resource = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(
                        "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"))) {
            assertEquals(
                    "dev.markodojkic.base.datasource.DataSourceAutoConfiguration",
                    new String(resource.readAllBytes(), StandardCharsets.UTF_8).trim());
        }
    }
}
