package org.example.sharing.common;

import lombok.Getter;
import lombok.Setter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Getter
@Setter
public class MinioContainer extends GenericContainer<MinioContainer> {

    private static final int DEFAULT_PORT = 9000;
    private static final String DEFAULT_USERNAME = "minioadmin";
    private static final String DEFAULT_PASSWORD = "minioadmin";
    private static final DockerImageName DEFAULT_IMAGE = DockerImageName.parse("minio/minio:latest");

    private String username;
    private String password;

    private static MinioContainer INSTANT;

    public static MinioContainer getInstance() {
        if(INSTANT == null){
            INSTANT = new MinioContainer();
        }
        return INSTANT;
    }

    public MinioContainer() {
        this(DEFAULT_IMAGE);
    }

    public MinioContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        this.username = DEFAULT_USERNAME;
        this.password = DEFAULT_PASSWORD;
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE);
        withCommand("server /data");
        withExposedPorts(DEFAULT_PORT);
        waitingFor(new HttpWaitStrategy()
                .forPort(DEFAULT_PORT)
                .forPath("/minio/health/ready")
                .withStartupTimeout(Duration.ofSeconds(10)));
    }

    public String getUrl() {
        return "http://" + getHost() + ':' + getMappedPort(DEFAULT_PORT);
    }

    @Override
    protected void configure() {
        withEnv("MINIO_ROOT_USER", this.username);
        withEnv("MINIO_ROOT_PASSWORD", this.password);
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("S3_URL", getUrl());
        System.setProperty("BUCKET_PHOTOS_NAME", "photos");
        System.setProperty("BUCKET_PHOTOS_ACCESS", getUsername());
        System.setProperty("BUCKET_PHOTOS_SECRET", getPassword());

        System.setProperty("BUCKET_DOCUMENTS_NAME", "documents");
        System.setProperty("BUCKET_DOCUMENTS_ACCESS", getUsername());
        System.setProperty("BUCKET_DOCUMENTS_SECRET", getPassword());
    }

    @Override
    public void stop() {
    }
}
