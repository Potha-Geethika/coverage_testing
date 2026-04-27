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
    void convertToLocalDateTime_validTimeFormat_returnsLocalDateTime() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime("12:30");
        assertEquals(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalDateTime.now().toLocalTime().withHour(12).withMinute(30)), result);
    }

    @Test
    void convertToLocalDateTime_invalidTimeFormat_returnsLocalDateTime() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime("20230101 12:30");
        assertEquals(LocalDateTime.parse("2023-01-01T12:30:00"), result);
    }

    @Test
    void convertToLocalDateTime_nullTime_returnsNull() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime(null);
        assertNull(result);
    }

    @Test
    void getTotalPumpTimeInMins_emptyList_returnsZero() {
        List<ActivityLogEntry> entries = new ArrayList<>();
        Float result = ActivityLogUtil.getTotalPumpTimeInMins(entries);
        assertEquals(0f, result);
    }

    @Test
    void getTotalPumpTimeInMins_singleEntry_returnsCorrectTotal() {
        ActivityLogEntry entry = new ActivityLogEntry();
        entry.setStart("12:00");
        entry.setEnd("12:30");
        entry.setComplete(true);
        List<ActivityLogEntry> entries = Collections.singletonList(entry);
        Float result = ActivityLogUtil.getTotalPumpTimeInMins(entries);
        assertEquals(30f, result);
    }

    @Test
    void round_floatValue_returnsRoundedValue() {
        Float result = ActivityLogUtil.round(2.34567f, 2);
        assertEquals(2.35f, result);
    }

    @Test
    void round_nullValue_returnsZero() {
        Float result = ActivityLogUtil.round((Float) null, 2);
        assertEquals(0f, result);
    }

    @Test
    void round_doubleValue_returnsRoundedValue() {
        Double result = ActivityLogUtil.round(2.34567, 2);
        assertEquals(2.35, result);
    }
}