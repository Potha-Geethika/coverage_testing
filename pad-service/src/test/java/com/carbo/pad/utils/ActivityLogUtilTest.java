package com.carbo.pad.utils;

import com.carbo.pad.model.ActivityLogEntry;
import java.io.*;
import java.nio.file.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




class ActivityLogUtilTest {

    @Test
    void testConvertToLocalDateTime_ValidTime() {
        String time = "12:30";
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime(time);
        Assertions.assertNotNull(result);
    }

    @Test
    void testConvertToLocalDateTime_InvalidLength() {
        String time = "123456";
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime(time);
        Assertions.assertNotNull(result);
    }

    @Test
    void testConvertToLocalDateTime_NullInput() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime(null);
        Assertions.assertNull(result);
    }

    @Test
    void testGetTotalPumpTimeInMilliSec_EmptyList() {
        List<ActivityLogEntry> entries = Collections.emptyList();
        Float result = ActivityLogUtil.getTotalPumpTimeInMilliSec(entries);
        Assertions.assertEquals(0f, result);
    }

    @Test
    void testGetTotalPumpTimeInMilliSec_ValidEntries() {
        ActivityLogEntry entry1 = Mockito.mock(ActivityLogEntry.class);
        Mockito.when(entry1.getMillisecondsSpan()).thenReturn(60000L); // 1 minute
        ActivityLogEntry entry2 = Mockito.mock(ActivityLogEntry.class);
        Mockito.when(entry2.getMillisecondsSpan()).thenReturn(120000L); // 2 minutes

        List<ActivityLogEntry> entries = List.of(entry1, entry2);
        Float result = ActivityLogUtil.getTotalPumpTimeInMilliSec(entries);
        Assertions.assertEquals(180000f, result); // 3 minutes
    }

    @Test
    void testGetTotalPumpTimeInMins_ValidEntries() {
        ActivityLogEntry entry = Mockito.mock(ActivityLogEntry.class);
        Mockito.when(entry.getMillisecondsSpan()).thenReturn(120000L); // 2 minutes

        List<ActivityLogEntry> entries = List.of(entry);
        Float result = ActivityLogUtil.getTotalPumpTimeInMins(entries);
        Assertions.assertEquals(2f, result);
    }

    @Test
    void testGetPumpTimeInMinsList_EmptyList() {
        List<ActivityLogEntry> entries = Collections.emptyList();
        List<Float> result = ActivityLogUtil.getPumpTimeInMinsList(entries);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetPumpTimeInMinsList_ValidEntries() {
        ActivityLogEntry entry1 = Mockito.mock(ActivityLogEntry.class);
        Mockito.when(entry1.getMillisecondsSpan()).thenReturn(60000L); // 1 minute
        Mockito.when(entry1.getStage()).thenReturn(1f);
        ActivityLogEntry entry2 = Mockito.mock(ActivityLogEntry.class);
        Mockito.when(entry2.getMillisecondsSpan()).thenReturn(120000L); // 2 minutes
        Mockito.when(entry2.getStage()).thenReturn(1f);
        ActivityLogEntry entry3 = Mockito.mock(ActivityLogEntry.class);
        Mockito.when(entry3.getMillisecondsSpan()).thenReturn(180000L); // 3 minutes
        Mockito.when(entry3.getStage()).thenReturn(2f);

        List<ActivityLogEntry> entries = List.of(entry1, entry2, entry3);
        List<Float> result = ActivityLogUtil.getPumpTimeInMinsList(entries);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void testRound_ValidDouble() {
        Double result = ActivityLogUtil.round(2.34567, 2);
        Assertions.assertEquals(2.35, result);
    }

    @Test
    void testRound_NullDouble() {
        Double result = ActivityLogUtil.round(null, 2);
        Assertions.assertEquals(0f, result);
    }

    @Test
    void testRound_ValidFloat() {
        Float result = ActivityLogUtil.round(2.34567f, 2);
        Assertions.assertEquals(2.35f, result);
    }

    @Test
    void testRound_NullFloat() {
        Float result = ActivityLogUtil.round(null, 2);
        Assertions.assertEquals(0f, result);
    }
}