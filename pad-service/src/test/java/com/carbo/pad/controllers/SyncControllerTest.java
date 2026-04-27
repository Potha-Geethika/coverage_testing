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
        request.addHeader("Organization-Id", "org123");
    }

    @Test
    void view_shouldReturn200AndResponseBody() throws Exception {
        Pad pad1 = new Pad();
        pad1.setId("pad1");
        pad1.setTs(1L);
        
        Pad pad2 = new Pad();
        pad2.setId("pad2");
        pad2.setTs(2L);
        
        when(padService.getByOrganizationId("org123")).thenReturn(Arrays.asList(pad1, pad2));
        
        mockMvc.perform(get("/v1/sync/view").requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pad1").value(1L))
                .andExpect(jsonPath("$.pad2").value(2L));
        
        verify(padService).getByOrganizationId("org123");
    }

    @Test
    void sync_shouldReturn200AndUpdatedResponse() throws Exception {
        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setUpdate(Arrays.asList(new Pad()));
        syncRequest.setRemove(new HashSet<>(Arrays.asList("pad1")));
        syncRequest.setGet(new HashSet<>(Arrays.asList("pad2")));

        Pad padToUpdate = new Pad();
        padToUpdate.setId("padToUpdate");
        padToUpdate.setTs(3L);
        when(padService.getPad("padToUpdate")).thenReturn(Optional.of(padToUpdate));

        Pad padToSave = new Pad();
        padToSave.setId("padToSave");
        when(padService.savePad(any(Pad.class))).thenReturn(padToSave);

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"update\":[{\"id\":\"padToUpdate\",\"ts\":1}],\"remove\":[\"pad1\"],\"get\":[\"pad2\"]}")
                .requestAttr("request", request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated.padToUpdate").value(3L))
                .andExpect(jsonPath("$.removed").isArray());

        verify(padService).deletePad("pad1");
        verify(padService).updatePad(any(Pad.class));
        verify(padService).savePad(any(Pad.class));
        verify(padService).getPad("padToUpdate");
    }

    @Test
    void sync_shouldReturn500OnEmptyOptional() throws Exception {
        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setUpdate(Arrays.asList(new Pad()));
        
        when(padService.getPad(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/v1/sync/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"update\":[{\"id\":\"padToUpdate\",\"ts\":1}]}"))
                .andExpect(status().isInternalServerError());

        verify(padService).getPad(any());
    }
}