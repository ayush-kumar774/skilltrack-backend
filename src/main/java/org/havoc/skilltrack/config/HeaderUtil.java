package org.havoc.skilltrack.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeaderUtil {

    private final ApiHeadersProperties props;

    public HttpHeaders getHeadersFor(String service) {
        ApiHeadersProperties.ServiceConfig serviceConfig = switch (service.toLowerCase()) {
            case "github" -> props.getGithub();
            case "leetcode" -> props.getLeetcode();
            default -> throw new IllegalArgumentException("Unknown service: " + service);
        };

        HttpHeaders headers = new HttpHeaders();
        serviceConfig.getHeaders().forEach(headers::add);
        return headers;
    }
}
