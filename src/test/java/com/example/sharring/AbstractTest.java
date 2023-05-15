package org.example.sharing;

import org.example.sharing.common.DatabaseContainer;
import org.example.sharing.common.MinioContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
public abstract class AbstractTest {
    @Container
    public static PostgreSQLContainer<DatabaseContainer> dbContainer = DatabaseContainer.getInstance();
    @Container
    public static GenericContainer<MinioContainer> minioContainer = MinioContainer.getInstance();
}
