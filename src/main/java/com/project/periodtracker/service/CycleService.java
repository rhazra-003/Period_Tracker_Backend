package com.project.periodtracker.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.periodtracker.constant.TrackerDataConstant;
import com.project.periodtracker.exception.NoCycleDataException;
import com.project.periodtracker.model.CycleEntry;
import com.project.periodtracker.model.User;
import com.project.periodtracker.repository.CycleRepository;
import com.project.periodtracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CycleService {

    private final CycleRepository cycleRepo;
    private final UserRepository userRepo;

    public void trackCycle(String email, LocalDate periodStart, int duration) {
        User user = validateAndGetUser(email);

        if (periodStart == null || periodStart.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(TrackerDataConstant.INVALID_PERIOD_START_DATE);
        }

        if (duration <= 0) {
            throw new IllegalArgumentException(TrackerDataConstant.INVALID_PERIOD_DURATION);
        }

        cycleRepo.save(new CycleEntry(null, periodStart, duration, null, user));
    }

    public List<Map<String, Object>> getRecentCycles(String email, int page, int size) {
        validateEmail(email);

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(TrackerDataConstant.INVALID_PAGINATION_PARAMETERS);
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by("periodStartDate").descending());
        List<CycleEntry> cycles = cycleRepo.findByUserEmailOrderByPeriodStartDateDesc(email, pageable);
        List<Map<String, Object>> result = new ArrayList<>();

        if (cycles.isEmpty()) {
            throw new NoCycleDataException(TrackerDataConstant.NO_CYCLE_DATA_FOUND);
        }

        for (int i = 0; i < cycles.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            CycleEntry current = cycles.get(i);
            map.put("id", current.getId());
            map.put("periodStart", current.getPeriodStartDate());
            map.put("duration", current.getDurationInDays());

            if (i + 1 < cycles.size()) {
                CycleEntry previous = cycles.get(i + 1);
                map.put("cycleLength",
                        ChronoUnit.DAYS.between(previous.getPeriodStartDate(), current.getPeriodStartDate()));
            }

            result.add(map);
        }
        return result;
    }

    public LocalDate predictNextPeriod(String email) {
        return predictCycleDetails(email).get(TrackerDataConstant.NEXT_PERIOD);
    }

    public Map<String, LocalDate> predictCycleDetails(String email) {
        validateEmail(email);

        List<LocalDate> dates = new ArrayList<>(cycleRepo.findLastFourPeriods(email));
        if (dates.size() < 2) {
            throw new IllegalArgumentException(TrackerDataConstant.NOT_ENOUGH_DATA_TO_PREDICT);
        }

        dates.sort(Comparator.naturalOrder());

        List<Long> gaps = new ArrayList<>();
        for (int i = 1; i < dates.size(); i++) {
            gaps.add(ChronoUnit.DAYS.between(dates.get(i - 1), dates.get(i)));
        }

        long avgCycleLength = Math.round(gaps.stream().mapToLong(x -> x).average().orElse(0));
        LocalDate nextPeriod = dates.get(dates.size() - 1).plusDays(avgCycleLength);
        LocalDate ovulationDate = nextPeriod.minusDays(14);
        LocalDate fertileWindowStart = ovulationDate.minusDays(3);
        LocalDate fertileWindowEnd = ovulationDate.plusDays(1);

        Map<String, LocalDate> prediction = new LinkedHashMap<>();
        prediction.put(TrackerDataConstant.NEXT_PERIOD, nextPeriod);
        prediction.put(TrackerDataConstant.OVULATION_DATE, ovulationDate);
        prediction.put(TrackerDataConstant.FERTILE_WINDOW_START, fertileWindowStart);
        prediction.put(TrackerDataConstant.FERTILE_WINDOW_END, fertileWindowEnd);
        return prediction;
    }

    public void deleteCycle(String email, Long id) {
        validateEmail(email);
        if (id == null) {
            throw new IllegalArgumentException(TrackerDataConstant.NOT_FOUND + ": cycle id");
        }

        CycleEntry cycle = cycleRepo.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new NoSuchElementException(TrackerDataConstant.NOT_FOUND + ": cycle id " + id));

        cycleRepo.delete(cycle);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(TrackerDataConstant.INVALID_EMAIL);
        }
    }

    private User validateAndGetUser(String email) {
        validateEmail(email);
        return userRepo.findById(email)
                .orElseThrow(() -> new NoSuchElementException(TrackerDataConstant.NOT_FOUND + ": " + email));
    }
}