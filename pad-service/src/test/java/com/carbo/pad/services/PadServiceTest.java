package com.carbo.pad.services;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;

import com.carbo.pad.events.source.PadTimezoneSourceBean;
import com.carbo.pad.model.Pad;
import com.carbo.pad.repository.PadMongoDbRepository;
import java.io.*;
import java.nio.file.*;
import java.security.Principal;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
class PadServiceTest {

    @Mock
    private PadMongoDbRepository padRepository;

    @Mock
    private PadTimezoneSourceBean padTimezoneSourceBean;

    @InjectMocks
    private PadService padService;

    private Pad pad;
    private Optional<Pad> optionalPad;

    @BeforeEach
    void setUp() {
        pad = new Pad();
        pad.setId("1");
        pad.setName("Test Pad");
        pad.setOrganizationId("org1");
        pad.setTimezone("UTC");
        optionalPad = Optional.of(pad);
    }

    @Test
    void testGetAll() {
        when(padRepository.findAll()).thenReturn(Collections.singletonList(pad));

        var result = padService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pad, result.get(0));
    }

    @Test
    void testGetByOrganizationId() {
        when(padRepository.findByOrganizationId("org1")).thenReturn(Collections.singletonList(pad));

        var result = padService.getByOrganizationId("org1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pad, result.get(0));
    }

    @Test
    void testGetPad() {
        when(padRepository.findById("1")).thenReturn(optionalPad);

        var result = padService.getPad("1");

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(pad, result.get());
    }

    @Test
    void testSavePad() {
        when(padRepository.save(any(Pad.class))).thenReturn(pad);

        var result = padService.savePad(pad);

        assertNotNull(result);
        assertEquals(pad, result);
    }

    @Test
    void testUpdatePad_TimezoneChanged() {
        Pad existingPad = new Pad();
        existingPad.setId("1");
        existingPad.setTimezone("PST");
        when(padRepository.findById("1")).thenReturn(Optional.of(existingPad));
        when(padRepository.save(any(Pad.class))).thenReturn(pad);

        padService.updatePad(pad);

        verify(padTimezoneSourceBean).publishPadTimezoneChange(eq("UPDATE"), eq(pad), eq("PST"));
        verify(padRepository).save(pad);
    }

    @Test
    void testUpdatePad_TimezoneNotChanged() {
        Pad existingPad = new Pad();
        existingPad.setId("1");
        existingPad.setTimezone("UTC");
        when(padRepository.findById("1")).thenReturn(Optional.of(existingPad));
        when(padRepository.save(any(Pad.class))).thenReturn(pad);

        padService.updatePad(pad);

        verify(padTimezoneSourceBean, never()).publishPadTimezoneChange(anyString(), any(Pad.class), anyString());
        verify(padRepository).save(pad);
    }

    @Test
    void testUpdatePad_NoExistingPad() {
        when(padRepository.findById("1")).thenReturn(Optional.empty());
        when(padRepository.save(any(Pad.class))).thenReturn(pad);

        padService.updatePad(pad);

        verify(padTimezoneSourceBean, never()).publishPadTimezoneChange(anyString(), any(Pad.class), anyString());
        verify(padRepository).save(pad);
    }

    @Test
    void testDeletePad() {
        doNothing().when(padRepository).deleteById("1");

        padService.deletePad("1");

        verify(padRepository).deleteById("1");
    }

    @Test
    void testGetByOrganizationIdIn() {
        when(padRepository.findByOrganizationIdIn(any(Set.class))).thenReturn(Collections.singletonList(pad));

        var result = padService.getByOrganizationIdIn(Set.of("org1"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pad, result.get(0));
    }
}