package com.carbo.pad.controllers;
import static org.mockito.ArgumentMatchers.any;

import com.carbo.pad.model.JobDTO;
import com.carbo.pad.model.Pad;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.*;
import java.util.stream.Collectors;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import static com.carbo.pad.utils.Constants.OPERATOR;
import static com.carbo.pad.utils.Constants.SHARED_ORGANIZATION_ID;
import static com.carbo.pad.utils.ControllerUtil.getOrganizationId;
import static com.carbo.pad.utils.ControllerUtil.getOrganizationType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;







@WebMvcTest(PadServiceController.class)
class PadServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PadService padService;

    @MockBean
    private MongoTemplate mongoTemplate;

    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        // Setup request attributes or headers if necessary
    }

    @Test
    void getPads_HappyPath() throws Exception {
        when(padService.getByOrganizationIdIn(any())).thenReturn(Collections.singletonList(new Pad()));

        mockMvc.perform(get("/v1/pads/").header("X-Organization-Id", "orgId"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    void getPad_HappyPath() throws Exception {
        Pad pad = new Pad();
        pad.setId("padId");
        when(padService.getPad("padId")).thenReturn(Optional.of(pad));

        mockMvc.perform(get("/v1/pads/padId"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("padId"));
    }

    @Test
    void getPad_NotFound() throws Exception {
        when(padService.getPad("padId")).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/pads/padId"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void updatePad_HappyPath() throws Exception {
        Pad pad = new Pad();
        pad.setId("padId");

        mockMvc.perform(put("/v1/pads/padId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"padId\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(padService).updatePad(any(Pad.class));
    }

    @Test
    void savePad_HappyPath() throws Exception {
        Pad pad = new Pad();
        pad.setId("padId");

        mockMvc.perform(post("/v1/pads/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"padId\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(padService).savePad(any(Pad.class));
    }

    @Test
    void deletePad_HappyPath() throws Exception {
        mockMvc.perform(delete("/v1/pads/padId"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(padService).deletePad("padId");
    }
}