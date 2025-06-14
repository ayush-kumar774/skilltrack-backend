package org.havoc.skilltrack.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

@Slf4j
@Component
public class HttpSender {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static <T> ResponseEntity<T> send(
            String caller,
            String serviceName,
            String url,
            HttpMethod method,
            HttpEntity<?> requestEntity,
            Class<T> responseType,
            boolean debugResponse,
            int timeoutMs
    ) {
        try {
            log.info("[{}] Sending request to {} - URL: {}", caller, serviceName, url);

            ResponseEntity<T> response = restTemplate.exchange(url, method, requestEntity, responseType);

            if (debugResponse) {
                log.debug("[{}] Received response from {} - Status: {}, Body: {}", caller, serviceName, response.getStatusCode(), response.getBody());
            } else {
                log.info("[{}] {} call completed - Status: {}", caller, serviceName, response.getStatusCode());
            }

            return response;

        } catch (HttpStatusCodeException ex) {
            log.error("[{}] {} call failed with status {} and body {}", caller, serviceName, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;

        } catch (Exception ex) {
            log.error("[{}] {} call failed: {}", caller, serviceName, ex.getMessage(), ex);
            throw new RuntimeException("Failed to call " + serviceName + ": " + ex.getMessage());
        }
    }

    public static <T> ResponseEntity<T> send(
            String context,
            String service,
            String url,
            HttpMethod method,
            HttpEntity<?> requestEntity,
            ParameterizedTypeReference<T> responseType,
            boolean debug,
            int timeout
    ) {
        RestTemplate restTemplate = new RestTemplate(); // ideally inject this
        ResponseEntity<T> response = restTemplate.exchange(url, method, requestEntity, responseType);

        if (debug) {
            log.debug("[{}:{}] URL: {}", context, service, url);
            log.debug("[{}:{}] Response: {}", context, service, response.getBody());
        }

        return response;
    }

}
