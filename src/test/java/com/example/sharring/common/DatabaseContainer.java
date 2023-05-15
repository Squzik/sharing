package org.example.sharing.common;

import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseContainer extends PostgreSQLContainer<DatabaseContainer> {

    private static final String IMAGE_VERSION = "postgres:12.7";
    private static final String DB_URL = "DB_URL";
    private static final String DB_USER = "DB_USER";
    private static final String DB_PASSWORD = "DB_PASSWORD";
    private static final String DB_DRIVER = "DB_DRIVER";

    private static DatabaseContainer INSTANT;

    private DatabaseContainer() {
        super(IMAGE_VERSION);
    }

    public static DatabaseContainer getInstance() {
        if (INSTANT == null) {
            INSTANT = new DatabaseContainer();
        }
        return INSTANT;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty(DB_URL, INSTANT.getJdbcUrl());
        System.setProperty(DB_USER, INSTANT.getUsername());
        System.setProperty(DB_PASSWORD, INSTANT.getPassword());
        System.setProperty(DB_DRIVER, INSTANT.getDriverClassName());
    }

    @Override
    public void stop() {
    }
}
