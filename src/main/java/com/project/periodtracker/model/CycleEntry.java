package com.project.periodtracker.model;

import java.time.LocalDate;

import com.project.periodtracker.constant.TrackerDataConstant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = TrackerDataConstant.TBL_CYCLES)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CycleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate periodStartDate;

    private int durationInDays;

    private Integer cycleLength;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = TrackerDataConstant.USER_ID, referencedColumnName = TrackerDataConstant.EMAIL)
    private User user;
}