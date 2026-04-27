package com.carbo.pad.services;
import static org.mockito.ArgumentMatchers.anyString;

import com.carbo.pad.model.*;
import com.google.gson.Gson;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.security.Principal;
import java.util.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.*;
import java.util.stream.Collectors;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;







@ExtendWith(MockitoExtension.class)
class AICallsServiceTest {

    @Mock
    private Client client;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Response response;

    private AICallsService aiCallsService;

    @BeforeEach
    void setUp() {
        aiCallsService = new AICallsService();
        // Mocking the newClient method to return our mocked client
        doReturn(client).when(aiCallsService).newClient();
    }

    @Test
    void testRetrieveFracproAuthTokenSuccess() throws URISyntaxException, ParseException {
        String expectedToken = "dummy-token";
        String userName = "username";
        String password = "password";
        aiCallsService.userName = userName;
        aiCallsService.password = password;
        aiCallsService.protocol = "http";
        aiCallsService.server = "localhost";
        aiCallsService.port = 8080;

        URI uri = new URI(aiCallsService.protocol, null, aiCallsService.server, aiCallsService.port, "/api/TokenAuth/AuthenticateNoTenant", null, null);
        when(client.target(uri)).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(webTarget);
        when(webTarget.header("Content-Type", "application/json")).thenReturn(webTarget);
        when(webTarget.post(Entity.json("{\"usernameOrEmailAddress\": \"" + userName + "\", \"password\": \"" + password + "\"}"))).thenReturn(response);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{\"result\":{\"accessToken\":\"" + expectedToken + "\"}}");
        
        String token = aiCallsService.retrieveFracproAuthToken();
        
        assertNotNull(token);
        assertEquals(expectedToken, token);
    }

    @Test
    void testGetAllFracproTreatmentsDirectWithNullTreatmentIds() {
        Map<Integer, FracProTreatment> result = aiCallsService.getAllFracproTreatmentsDirect(1, null, "token");
        
        assertNotNull(result);
        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    void testGetAllFracproTreatmentsDirectWithEmptyTreatmentIds() {
        Map<Integer, FracProTreatment> result = aiCallsService.getAllFracproTreatmentsDirect(1, Collections.emptyList(), "token");
        
        assertNotNull(result);
        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    void testGetAllFracproTreatmentsDirectSuccess() {
        List<FracProTreatmentId> treatmentIds = List.of(new FracProTreatmentId(1), new FracProTreatmentId(2));
        Map<Integer, FracProTreatment> expectedMap = new HashMap<>();
        expectedMap.put(1, new FracProTreatment("1", "Test Treatment 1"));
        expectedMap.put(2, new FracProTreatment("2", "Test Treatment 2"));

        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{\"result\":[{\"name\":\"Test Treatment 1\"},{\"name\":\"Test Treatment 2\"}]}");

        for (FracProTreatmentId treatmentId : treatmentIds) {
            when(client.target(anyString())).thenReturn(webTarget);
            when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(webTarget);
            when(webTarget.header("Content-Type", "application/json")).thenReturn(webTarget);
            when(webTarget.header("Authorization", "Bearer token")).thenReturn(webTarget);
            when(webTarget.get()).thenReturn(response);
        }

        Map<Integer, FracProTreatment> result = aiCallsService.getAllFracproTreatmentsDirect(1, treatmentIds, "token");
        
        assertNotNull(result);
        assertEquals(expectedMap, result);
    }

    @Test
    void testGetFracProTreatmentDirectSuccess() {
        int wellId = 1;
        int treatmentId = 1;
        String token = "token";
        boolean isSummary = true;
        String expectedName = "Test Treatment";

        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{\"result\":{\"name\":\"" + expectedName + "\"}}");
        
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(webTarget);
        when(webTarget.header("Content-Type", "application/json")).thenReturn(webTarget);
        when(webTarget.header("Authorization", "Bearer " + token)).thenReturn(webTarget);
        when(webTarget.get()).thenReturn(response);

        FracProTreatment treatment = aiCallsService.getFracProTreatmentDirect(wellId, treatmentId, token, isSummary);
        
        assertNotNull(treatment);
        assertEquals(expectedName, treatment.getName());
    }
}