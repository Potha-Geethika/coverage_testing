package com.carbo.pad.controllers;

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
class SyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PadService padService;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.addHeader("Organization-Id", "org1");
    }

    @Test
    void view_ShouldReturn200AndMap() throws Exception {
        List<Pad> pads = Arrays.asList(new Pad(), new Pad());
        pads.get(0).setId("pad1");
        pads.get(0).setTs(1L);
        pads.get(1).setId("pad2");
        pads.get(1).setTs(2L);

        when(padService.getByOrganizationId("org1")).thenReturn(pads);

        mockMvc.perform(get("/v1/sync/view")
                .requestAttr("organizationId", "org1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pad1").value(1))
                .andExpect(jsonPath("$.pad2").value(2));
    }

    @Test
    void sync_ShouldReturn200AndSyncResponse() throws Exception {
        SyncRequest syncRequest = new SyncRequest();
        Set<String> removeIds = new HashSet<>(Arrays.asList("pad1", "pad2"));
        syncRequest.setRemove(removeIds);

        when(padService.getPad("pad1")).thenReturn(Optional.of(new Pad()));
        when(padService.getPad("pad2")).thenReturn(Optional.of(new Pad()));
        
        doNothing().when(padService).deletePad("pad1");
        doNothing().when(padService).deletePad("pad2");

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"remove\":[\"pad1\", \"pad2\"]}")
                .requestAttr("organizationId", "org1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.removed").isArray())
                .andExpect(jsonPath("$.removed").value(Arrays.asList("pad1", "pad2")));

        // Verify interactions with the service
        Mockito.verify(padService).deletePad("pad1");
        Mockito.verify(padService).deletePad("pad2");
    }

    @Test
    void sync_ShouldReturn500_WhenPadNotFound() throws Exception {
        SyncRequest syncRequest = new SyncRequest();
        List<Pad> updates = Collections.singletonList(new Pad());
        updates.get(0).setId("pad3");
        updates.get(0).setOrganizationId("org1");
        syncRequest.setUpdate(updates);

        when(padService.getPad("pad3")).thenReturn(Optional.empty());

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"update\":[{\"id\":\"pad3\", \"organizationId\":\"org1\"}]}"))
                .andExpect(status().isInternalServerError());
    }
}