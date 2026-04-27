package com.carbo.pad.services;
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

    @BeforeEach
    void setUp() {
        pad = new Pad();
        pad.setId("1");
        pad.setName("Pad 1");
        pad.setTimezone("UTC");
        pad.setOperatorId("operator1");
        pad.setOrganizationId("org1");
    }

    @Test
    void getAll() {
        when(padRepository.findAll()).thenReturn(Collections.singletonList(pad));
        assertNotNull(padService.getAll());
        assertEquals(1, padService.getAll().size());
    }

    @Test
    void getByOrganizationId() {
        when(padRepository.findByOrganizationId("org1")).thenReturn(Collections.singletonList(pad));
        assertNotNull(padService.getByOrganizationId("org1"));
        assertEquals(1, padService.getByOrganizationId("org1").size());
    }

    @Test
    void getPad() {
        when(padRepository.findById("1")).thenReturn(Optional.of(pad));
        Optional<Pad> result = padService.getPad("1");
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void savePad() {
        when(padRepository.save(pad)).thenReturn(pad);
        Pad result = padService.savePad(pad);
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    void updatePad() {
        when(padRepository.findById("1")).thenReturn(Optional.of(pad));
        padService.updatePad(pad);
        verify(padTimezoneSourceBean, times(0)).publishPadTimezoneChange(any(), any(), any());
        verify(padRepository, times(1)).save(pad);
    }

    @Test
    void updatePad_TimezoneChanged() {
        Pad existingPad = new Pad();
        existingPad.setTimezone("PST");
        when(padRepository.findById("1")).thenReturn(Optional.of(existingPad));
        pad.setTimezone("UTC");
        padService.updatePad(pad);
        verify(padTimezoneSourceBean, times(1)).publishPadTimezoneChange(eq("UPDATE"), eq(pad), eq("PST"));
        verify(padRepository, times(1)).save(pad);
    }

    @Test
    void deletePad() {
        padService.deletePad("1");
        verify(padRepository, times(1)).deleteById("1");
    }

    @Test
    void getByOrganizationIdIn() {
        when(padRepository.findByOrganizationIdIn(Set.of("org1"))).thenReturn(Collections.singletonList(pad));
        assertNotNull(padService.getByOrganizationIdIn(Set.of("org1")));
        assertEquals(1, padService.getByOrganizationIdIn(Set.of("org1")).size());
    }

    @Test
    void updatePad_NullTimezonePresent() {
        Pad existingPad = new Pad();
        existingPad.setTimezone(null);
        when(padRepository.findById("1")).thenReturn(Optional.of(existingPad));
        pad.setTimezone("UTC");
        padService.updatePad(pad);
        verify(padTimezoneSourceBean, times(1)).publishPadTimezoneChange(eq("UPDATE"), eq(pad), isNull());
        verify(padRepository, times(1)).save(pad);
    }
}