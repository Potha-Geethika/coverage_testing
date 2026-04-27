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
    void testConvertToLocalDateTime_ValidTime_HHMM() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime("12:30");
        LocalDateTime expected = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.parse("12:30"));
        assertEquals(expected.getHour(), result.getHour());
        assertEquals(expected.getMinute(), result.getMinute());
    }

    @Test
    void testConvertToLocalDateTime_ValidTime_yyyyMMdd_HHMM() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime("20230101 12:30");
        LocalDateTime expected = LocalDateTime.parse("2023-01-01T12:30");
        assertEquals(expected, result);
    }

    @Test
    void testConvertToLocalDateTime_NullInput() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime(null);
        assertNull(result);
    }

    @Test
    void testGetTotalPumpTimeInMins_EmptyList() {
        Float result = ActivityLogUtil.getTotalPumpTimeInMins(new ArrayList<>());
        assertEquals(0.0f, result);
    }

    @Test
    void testGetTotalPumpTimeInMins_ValidEntries() {
        List<ActivityLogEntry> entries = new ArrayList<>();
        ActivityLogEntry entry1 = new ActivityLogEntry();
        entry1.setStart("12:00");
        entry1.setEnd("12:30");
        entries.add(entry1);

        ActivityLogEntry entry2 = new ActivityLogEntry();
        entry2.setStart("13:00");
        entry2.setEnd("13:30");
        entries.add(entry2);

        Float result = ActivityLogUtil.getTotalPumpTimeInMins(entries);
        assertEquals(60.0f, result);
    }

    @Test
    void testGetPumpTimeInMinsList_EmptyList() {
        List<Float> result = ActivityLogUtil.getPumpTimeInMinsList(new ArrayList<>());
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPumpTimeInMinsList_ValidEntries() {
        List<ActivityLogEntry> entries = new ArrayList<>();
        ActivityLogEntry entry1 = new ActivityLogEntry();
        entry1.setStart("12:00");
        entry1.setEnd("12:30");
        entry1.setStage(1.0f);
        entries.add(entry1);

        ActivityLogEntry entry2 = new ActivityLogEntry();
        entry2.setStart("13:00");
        entry2.setEnd("13:30");
        entry2.setStage(1.0f);
        entries.add(entry2);

        List<Float> result = ActivityLogUtil.getPumpTimeInMinsList(entries);
        assertEquals(2, result.size());
    }

    @Test
    void testRound_NullDoubleInput() {
        Double result = ActivityLogUtil.round((Double) null, 2);
        assertNull(result);
    }

    @Test
    void testRound_NullFloatInput() {
        Float result = ActivityLogUtil.round((Float) null, 2);
        assertEquals(0f, result);
    }

    @Test
    void testRound_ValidDoubleInput() {
        Double result = ActivityLogUtil.round(2.12345, 2);
        assertEquals(2.12, result);
    }

    @Test
    void testRound_ValidFloatInput() {
        Float result = ActivityLogUtil.round(2.12345f, 2);
        assertEquals(2.12f, result);
    }
}