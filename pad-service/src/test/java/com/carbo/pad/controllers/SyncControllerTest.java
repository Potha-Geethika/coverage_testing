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
class SyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PadService padService;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        when(request.getHeader("Organization-Id")).thenReturn("test-org-id");
    }

    @Test
    void view_shouldReturn200_WithValidRequest() throws Exception {
        List<Pad> pads = new ArrayList<>();
        Pad pad1 = new Pad();
        pad1.setId("pad1");
        pad1.setTs(1L);
        pads.add(pad1);

        when(padService.getByOrganizationId("test-org-id")).thenReturn(pads);

        mockMvc.perform(get("/v1/sync/view")
                .requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pad1").value(1L));

        verify(padService).getByOrganizationId("test-org-id");
    }

    @Test
    void sync_shouldReturn200_WithValidRequest() throws Exception {
        SyncRequest syncRequest = new SyncRequest();
        List<Pad> updatePads = new ArrayList<>();
        Pad pad = new Pad();
        pad.setId("pad1");
        pad.setTs(1L);
        pad.setOrganizationId("test-org-id");
        updatePads.add(pad);
        syncRequest.setUpdate(updatePads);
        syncRequest.setRemove(Set.of("pad2"));
        syncRequest.setGet(Set.of("pad3"));

        when(padService.getPad("pad1")).thenReturn(Optional.of(pad));
        when(padService.savePad(any(Pad.class))).thenReturn(pad);
        doNothing().when(padService).deletePad("pad2");

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"update\":[{\"id\":\"pad1\",\"ts\":1,\"organizationId\":\"test-org-id\"}],\"remove\":[\"pad2\"],\"get\":[\"pad3\"]}")
                .requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated.pad1").value(1L))
                .andExpect(jsonPath("$.removed[0]").value("pad2"));

        verify(padService).deletePad("pad2");
        verify(padService).updatePad(any(Pad.class));
        verify(padService).savePad(any(Pad.class));
    }

    @Test
    void sync_shouldReturn500_WhenServiceThrowsException() throws Exception {
        SyncRequest syncRequest = new SyncRequest();
        List<Pad> updatePads = new ArrayList<>();
        Pad pad = new Pad();
        pad.setId("pad1");
        pad.setTs(1L);
        pad.setOrganizationId("test-org-id");
        updatePads.add(pad);
        syncRequest.setUpdate(updatePads);

        when(padService.getPad("pad1")).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"update\":[{\"id\":\"pad1\",\"ts\":1,\"organizationId\":\"test-org-id\"}]}") // simplified JSON
                .requestAttr("request", request))
                .andExpect(status().isInternalServerError());

        verify(padService).getPad("pad1");
    }

    @Test
    void sync_shouldReturn400_WhenValidationFails() throws Exception {
        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setUpdate(Collections.singletonList(new Pad())); // Pad with no ID or organizationId

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"update\":[{\"id\":\"\",\"ts\":1}],\"remove\":[],\"get\":[]}")
                .requestAttr("request", request))
                .andExpect(status().isBadRequest());
    }
}