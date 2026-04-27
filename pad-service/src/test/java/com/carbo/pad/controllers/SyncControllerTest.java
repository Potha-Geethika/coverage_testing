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
public class SyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PadService padService;

    private SyncRequest syncRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        syncRequest = new SyncRequest();
        syncRequest.setUpdate(new ArrayList<>());
        syncRequest.setRemove(new HashSet<>());
        syncRequest.setGet(new HashSet<>());
    }

    @Test
    public void view_shouldReturnPadsTsMap_whenRequestIsValid() throws Exception {
        // Setup
        String organizationId = "org1";
        Pad pad1 = new Pad();
        pad1.setId("pad1");
        pad1.setTs(123L);
        
        when(padService.getByOrganizationId(organizationId)).thenReturn(Collections.singletonList(pad1));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("organizationId", organizationId);

        // Execute and Verify
        mockMvc.perform(get("/v1/sync/view")
                        .requestAttr("organizationId", organizationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pad1").value(123L));

        verify(padService, times(1)).getByOrganizationId(organizationId);
    }

    @Test
    public void sync_shouldReturnUpdated_whenUpdatesAreSuccessful() throws Exception {
        // Setup
        String organizationId = "org1";
        Pad padToUpdate = new Pad();
        padToUpdate.setId("pad1");
        padToUpdate.setTs(100L);
        padToUpdate.setOrganizationId(organizationId);
        
        Pad existingPad = new Pad();
        existingPad.setId("pad1");
        existingPad.setTs(200L);
        
        syncRequest.getUpdate().add(padToUpdate);
        
        when(padService.getPad(padToUpdate.getId())).thenReturn(Optional.of(existingPad));
        
        // Execute and Verify
        mockMvc.perform(post("/v1/sync/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"update\":[{\"id\":\"pad1\",\"ts\":100,\"organizationId\":\"org1\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated.pad1").value(existingPad.getTs()));

        verify(padService, times(1)).getPad(padToUpdate.getId());
        verify(padService, times(1)).updatePad(padToUpdate);
    }

    @Test
    public void sync_shouldReturnInternalServerError_whenGetPadReturnsEmpty() throws Exception {
        // Setup
        Pad padToUpdate = new Pad();
        padToUpdate.setId("pad1");
        padToUpdate.setTs(100L);
        
        syncRequest.getUpdate().add(padToUpdate);
        
        when(padService.getPad(padToUpdate.getId())).thenReturn(Optional.empty());

        // Execute and Verify
        mockMvc.perform(post("/v1/sync/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"update\":[{\"id\":\"pad1\",\"ts\":100}]}"))
                .andExpect(status().isInternalServerError());

        verify(padService, times(1)).getPad(padToUpdate.getId());
    }
}