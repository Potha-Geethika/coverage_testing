package com.carbo.pad.controllers;
import static org.mockito.ArgumentMatchers.any;

import com.carbo.pad.model.Pad;
import com.carbo.pad.model.SyncRequest;
import com.carbo.pad.model.SyncResponse;
import com.carbo.pad.services.PadService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.*;
import java.nio.file.*;
import java.security.Principal;
import java.time.*;
import java.util.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import static com.carbo.pad.utils.ControllerUtil.getOrganizationId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;







@WebMvcTest(SyncController.class)
public class SyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PadService padService;

    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        // Mocking the HttpServletRequest to return a specified organizationId
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Organization-Id")).thenReturn("org123");
    }

    @Test
    public void view_ShouldReturn200_WithPads() throws Exception {
        // Prepare mock data
        List<Pad> pads = new ArrayList<>();
        Pad pad1 = new Pad();
        pad1.setId("pad1");
        pad1.setTs(123L);
        pads.add(pad1);

        when(padService.getByOrganizationId("org123")).thenReturn(pads);

        mockMvc.perform(get("/v1/sync/view")
                .header("Organization-Id", "org123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pad1").value(123L));

        Mockito.verify(padService).getByOrganizationId("org123");
    }

    @Test
    public void sync_ShouldReturn200_WithUpdatedPads() throws Exception {
        // Prepare mock data
        Pad padToUpdate = new Pad();
        padToUpdate.setId("pad1");
        padToUpdate.setTs(123L);
        padToUpdate.setOrganizationId("org123");

        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setUpdate(Collections.singletonList(padToUpdate));
        syncRequest.setRemove(Collections.emptySet());
        syncRequest.setGet(Collections.emptySet());

        when(padService.getPad("pad1")).thenReturn(Optional.of(padToUpdate));
        doNothing().when(padService).updatePad(any());
        when(padService.savePad(any())).thenReturn(padToUpdate);

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"update\":[{\"id\":\"pad1\",\"ts\":123}],\"remove\":[],\"get\":[]}")
                .header("Organization-Id", "org123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated.pad1").value(123L));

        Mockito.verify(padService).updatePad(any());
        Mockito.verify(padService).getPad("pad1");
    }

    @Test
    public void sync_ShouldReturn500_WhenGetPadFails() throws Exception {
        // Prepare mock data
        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setUpdate(Collections.emptyList());
        syncRequest.setRemove(Collections.emptySet());
        syncRequest.setGet(Collections.singleton("pad1"));

        when(padService.getPad("pad1")).thenReturn(Optional.empty());

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"update\":[],\"remove\":[],\"get\":[\"pad1\"]}")
                .header("Organization-Id", "org123"))
                .andExpect(status().isOk()) // Adjust expected status depending on your controller logic
                .andExpect(jsonPath("$.get").isEmpty());

        Mockito.verify(padService).getPad("pad1");
    }
}