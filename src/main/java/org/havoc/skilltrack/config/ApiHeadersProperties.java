package org.havoc.skilltrack.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "external")
public class ApiHeadersProperties {
    private ServiceConfig github;
    private ServiceConfig leetcode;

    @Getter
    @Setter
    public static class ServiceConfig {
        private String baseUrl;
        private Map<String, String> headers;
    }
}
