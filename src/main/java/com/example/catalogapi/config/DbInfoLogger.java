package com.example.catalogapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Component
public class DbInfoLogger {
    private static final Logger log = LoggerFactory.getLogger(DbInfoLogger.class);
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DbInfoLogger(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logDbInfo() {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            log.info("DB connection: url={} | user={} | product={} {}", meta.getURL(), meta.getUserName(), meta.getDatabaseProductName(), meta.getDatabaseProductVersion());
        } catch (Exception ex) {
            log.warn("Failed to inspect DB metadata", ex);
        }

        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
            log.info("products table row count: {}", count);
        } catch (Exception ex) {
            log.warn("Could not count products table (table may not exist yet)", ex.getMessage());
        }
    }
}
