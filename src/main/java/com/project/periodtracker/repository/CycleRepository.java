package com.project.periodtracker.repository;

import java.time.LocalDate;
import java.util.List;

import com.project.periodtracker.constant.TrackerSQLConstant;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.periodtracker.model.CycleEntry;

public interface CycleRepository extends JpaRepository<CycleEntry, Long> {
    
    List<CycleEntry> findByUserEmailOrderByPeriodStartDateDesc(String email, Pageable pageable);

    java.util.Optional<CycleEntry> findByIdAndUserEmail(Long id, String email);

    @Query(TrackerSQLConstant.FETCH_LAST_FOUR_PERIOD_DATES)

    List<LocalDate> findLastFourPeriods(String email);
}