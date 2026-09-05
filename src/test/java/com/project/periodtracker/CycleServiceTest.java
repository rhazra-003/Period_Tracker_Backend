package com.project.periodtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.periodtracker.repository.CycleRepository;
import com.project.periodtracker.repository.UserRepository;
import com.project.periodtracker.constant.TrackerDataConstant;
import com.project.periodtracker.model.CycleEntry;
import com.project.periodtracker.service.CycleService;

@ExtendWith(MockitoExtension.class)
class CycleServiceTest {

    @Mock
    private CycleRepository cycleRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private CycleService cycleService;

    @Test
    void predictCycleDetails_usesAverageCycleLength_forNextPeriodOvulationAndFertileWindow() {
        String email = "test@example.com";
        when(cycleRepo.findLastFourPeriods(email)).thenReturn(List.of(
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 7, 14),
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 5, 4)
        ));

        Map<String, LocalDate> prediction = cycleService.predictCycleDetails(email);
        LocalDate expectedNextPeriod = LocalDate.of(2026, 8, 20).plusDays(36);
        LocalDate expectedOvulationDate = expectedNextPeriod.minusDays(14);

        assertEquals(expectedNextPeriod, prediction.get("nextPeriod"));
        assertEquals(expectedOvulationDate, prediction.get("ovulationDate"));
        assertEquals(expectedOvulationDate.minusDays(2), prediction.get("fertileWindowStart"));
        assertEquals(expectedOvulationDate.plusDays(2), prediction.get("fertileWindowEnd"));
    }

    @Test
    void predictCycleDetails_requiresAtLeastThreePeriodDates() {
        String email = "test@example.com";
        when(cycleRepo.findLastFourPeriods(email)).thenReturn(List.of(
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 7, 14)
        ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cycleService.predictCycleDetails(email));

        assertEquals(TrackerDataConstant.NOT_ENOUGH_DATA_TO_PREDICT, exception.getMessage());
    }

    @Test
    void predictMonthlyCycle_returnsAllPhasesOverlappingSelectedMonth() {
        String email = "test@example.com";
        when(cycleRepo.findLastFourCycles(email)).thenReturn(List.of(
                new CycleEntry(1L, LocalDate.of(2026, 1, 1), 5, null, null),
                new CycleEntry(2L, LocalDate.of(2026, 1, 29), 5, null, null),
                new CycleEntry(3L, LocalDate.of(2026, 2, 26), 5, null, null)
        ));

        Map<String, Object> forecast = cycleService.predictMonthlyCycle(email, YearMonth.of(2026, 3));

        assertEquals(28L, forecast.get("averageCycleLength"));
        assertEquals(5, ((List<?>) forecast.get("phases")).size());
    }
}
