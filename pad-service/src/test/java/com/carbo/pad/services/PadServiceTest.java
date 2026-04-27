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
        pad.setOperatorId("operator-1");
        pad.setOrganizationId("org-1");
    }

    @Test
    void getAll() {
        when(padRepository.findAll()).thenReturn(Collections.singletonList(pad));
        List<Pad> pads = padService.getAll();
        assertNotNull(pads);
        assertEquals(1, pads.size());
        assertEquals(pad, pads.get(0));
    }

    @Test
    void getByOrganizationId() {
        when(padRepository.findByOrganizationId("org-1")).thenReturn(Collections.singletonList(pad));
        List<Pad> pads = padService.getByOrganizationId("org-1");
        assertNotNull(pads);
        assertEquals(1, pads.size());
        assertEquals(pad, pads.get(0));
    }

    @Test
    void getPad() {
        when(padRepository.findById("1")).thenReturn(Optional.of(pad));
        Optional<Pad> retrievedPad = padService.getPad("1");
        assertTrue(retrievedPad.isPresent());
        assertEquals(pad, retrievedPad.get());
    }

    @Test
    void savePad() {
        when(padRepository.save(any(Pad.class))).thenReturn(pad);
        Pad savedPad = padService.savePad(pad);
        assertNotNull(savedPad);
        assertEquals(pad, savedPad);
    }

    @Test
    void updatePad_TimezoneChanged() {
        Pad existingPad = new Pad();
        existingPad.setId("1");
        existingPad.setTimezone("PST");
        when(padRepository.findById("1")).thenReturn(Optional.of(existingPad));
        doNothing().when(padTimezoneSourceBean).publishPadTimezoneChange(any(), any(), any());
        padService.updatePad(pad);
        verify(padTimezoneSourceBean, times(1)).publishPadTimezoneChange("UPDATE", pad, "PST");
        verify(padRepository, times(1)).save(pad);
    }

    @Test
    void updatePad_NoTimezoneChange() {
        Pad existingPad = new Pad();
        existingPad.setId("1");
        existingPad.setTimezone("UTC");
        when(padRepository.findById("1")).thenReturn(Optional.of(existingPad));
        padService.updatePad(pad);
        verify(padTimezoneSourceBean, never()).publishPadTimezoneChange(any(), any(), any());
        verify(padRepository, times(1)).save(pad);
    }

    @Test
    void updatePad_PadNotFound() {
        when(padRepository.findById("1")).thenReturn(Optional.empty());
        padService.updatePad(pad);
        verify(padTimezoneSourceBean, never()).publishPadTimezoneChange(any(), any(), any());
        verify(padRepository, times(1)).save(pad);
    }

    @Test
    void deletePad() {
        doNothing().when(padRepository).deleteById("1");
        padService.deletePad("1");
        verify(padRepository, times(1)).deleteById("1");
    }

    @Test
    void getByOrganizationIdIn() {
        when(padRepository.findByOrganizationIdIn(Set.of("org-1"))).thenReturn(Collections.singletonList(pad));
        List<Pad> pads = padService.getByOrganizationIdIn(Set.of("org-1"));
        assertNotNull(pads);
        assertEquals(1, pads.size());
        assertEquals(pad, pads.get(0));
    }
}