package com.example.stemlinkapp;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import org.testcontainers.dockerclient.DockerClientProviderStrategy;
import org.testcontainers.dockerclient.TransportConfig;

import java.net.URI;

public class FixedApiVersionDockerStrategy extends DockerClientProviderStrategy {

    @Override
    public TransportConfig getTransportConfig() {
        return TransportConfig.builder()
                .dockerHost(URI.create("unix:///var/run/docker.sock"))
                .build();
    }

    @Override
    public DockerClientConfig getDockerClientConfig() {
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .withApiVersion("1.41")
                .build();
    }

    @Override
    public String getDescription() {
        return "Fixed API Version 1.41 Strategy for Docker 29.x";
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
