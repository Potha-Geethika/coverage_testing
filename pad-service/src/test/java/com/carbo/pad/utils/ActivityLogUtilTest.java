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
    void convertToLocalDateTime_validTimeFormat() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime("12:30");
        assertNotNull(result);
        assertEquals(12, result.getHour());
        assertEquals(30, result.getMinute());
    }

    @Test
    void convertToLocalDateTime_invalidTimeFormat() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime("20230101 12:30");
        assertNotNull(result);
    }

    @Test
    void convertToLocalDateTime_nullInput() {
        LocalDateTime result = ActivityLogUtil.convertToLocalDateTime(null);
        assertEquals(null, result);
    }

    @Test
    void getTotalPumpTimeInMins_emptyList() {
        Float result = ActivityLogUtil.getTotalPumpTimeInMins(Collections.emptyList());
        assertEquals(0.0f, result);
    }

    @Test
    void getTotalPumpTimeInMins_validEntries() {
        List<ActivityLogEntry> entries = new ArrayList<>();
        ActivityLogEntry entry1 = new ActivityLogEntry();
        entry1.setStart("12:00");
        entry1.setEnd("12:30");
        entries.add(entry1);

        ActivityLogEntry entry2 = new ActivityLogEntry();
        entry2.setStart("13:00");
        entry2.setEnd("13:15");
        entries.add(entry2);

        Float result = ActivityLogUtil.getTotalPumpTimeInMins(entries);
        assertEquals(45.0f, result);
    }

    @Test
    void getPumpTimeInMinsList_emptyList() {
        List<Float> result = ActivityLogUtil.getPumpTimeInMinsList(Collections.emptyList());
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void getPumpTimeInMinsList_validEntries() {
        List<ActivityLogEntry> entries = new ArrayList<>();
        ActivityLogEntry entry1 = new ActivityLogEntry();
        entry1.setStage(1.0f);
        entry1.setStart("12:00");
        entry1.setEnd("12:30");
        entries.add(entry1);

        ActivityLogEntry entry2 = new ActivityLogEntry();
        entry2.setStage(1.0f);
        entry2.setStart("13:00");
        entry2.setEnd("13:15");
        entries.add(entry2);

        List<Float> result = ActivityLogUtil.getPumpTimeInMinsList(entries);
        assertEquals(2, result.size());
    }

    @Test
    void round_floatInputNull() {
        Float result = ActivityLogUtil.round((Float) null, 2);
        assertEquals(0f, result);
    }

    @Test
    void round_floatInputValid() {
        Float result = ActivityLogUtil.round(2.34567f, 2);
        assertEquals(2.35f, result);
    }

    @Test
    void round_doubleInputValid() {
        Double result = ActivityLogUtil.round(2.34567, 2);
        assertEquals(2.35, result);
    }

    @Test
    void getTotalPumpTimeInMilliSec_emptyList() {
        Float result = ActivityLogUtil.getTotalPumpTimeInMilliSec(Collections.emptyList());
        assertEquals(0.0f, result);
    }

    @Test
    void getTotalPumpTimeInMilliSec_validEntries() {
        List<ActivityLogEntry> entries = new ArrayList<>();
        ActivityLogEntry entry1 = new ActivityLogEntry();
        entry1.setStart("12:00");
        entry1.setEnd("12:30");
        entries.add(entry1);

        ActivityLogEntry entry2 = new ActivityLogEntry();
        entry2.setStart("13:00");
        entry2.setEnd("13:15");
        entries.add(entry2);

        Float result = ActivityLogUtil.getTotalPumpTimeInMilliSec(entries);
        assertEquals(2700000.0f, result);
    }
}