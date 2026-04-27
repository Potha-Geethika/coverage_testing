package com.carbo.pad.services;

import java.io.*;
import java.nio.file.*;
import java.security.Principal;
import java.util.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;






@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock private RestTemplate restTemplate;
    @Value("${security.oauth2.resource.userInfoUri}") private String USER_API_URL = "http://example.com/userinfo";

    @InjectMocks
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient();
        userClient.restTemplate = restTemplate;
    }

    @Test
    void getUserInfo_HappyPath() {
        String accessToken = "valid-access-token";
        Map<String, Object> expectedResponse = Map.of("id", "123", "name", "John Doe");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(expectedResponse);
        when(restTemplate.exchange(USER_API_URL, HttpMethod.GET, requestEntity, Map.class)).thenReturn(responseEntity);

        Map<String, Object> actualResponse = userClient.getUserInfo(accessToken);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserInfo_EmptyResponse() {
        String accessToken = "valid-access-token";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(Collections.emptyMap());
        when(restTemplate.exchange(USER_API_URL, HttpMethod.GET, requestEntity, Map.class)).thenReturn(responseEntity);

        Map<String, Object> actualResponse = userClient.getUserInfo(accessToken);

        assertNotNull(actualResponse);
        assertEquals(Collections.emptyMap(), actualResponse);
    }

    @Test
    void getUserInfo_ErrorHandling() {
        String accessToken = "valid-access-token";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        when(restTemplate.exchange(USER_API_URL, HttpMethod.GET, requestEntity, Map.class))
                .thenThrow(new RuntimeException("Error during REST call"));

        // This would typically be caught and handled in a real application
        // Here we will just call the method to verify the exception is thrown
        Exception exception = null;
        try {
            userClient.getUserInfo(accessToken);
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("Error during REST call", exception.getMessage());
    }

    @Test
    void getUserInfo_NullAccessToken() {
        Exception exception = null;
        try {
            userClient.getUserInfo(null);
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("Access token cannot be null", exception.getMessage());
    }
}