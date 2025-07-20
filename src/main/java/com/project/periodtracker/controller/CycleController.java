package com.project.periodtracker.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.periodtracker.constant.TrackerDataConstant;
import com.project.periodtracker.model.User;
import com.project.periodtracker.repository.UserRepository;
import com.project.periodtracker.service.CycleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cycles")
@RequiredArgsConstructor
public class CycleController {

    private final CycleService cycleService;
    private final UserRepository userRepo;

    @PostMapping("/track")
    public ResponseEntity<?> track(@RequestParam String email,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                   @RequestParam int duration) {
        if (!userRepo.existsById(email)) {
            userRepo.save(new User(email, TrackerDataConstant.NAME, Instant.now()));
        }
        cycleService.trackCycle(email, start, duration);
        return ResponseEntity.ok(TrackerDataConstant.CYCLE_TRACKED);
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getCycles(@RequestParam String email,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cycleService.getRecentCycles(email, page, size));
    }

    @GetMapping("/predict")
    public ResponseEntity<?> predict(@RequestParam String email) {
        LocalDate prediction = cycleService.predictNextPeriod(email);
        return ResponseEntity.ok(Map.of(TrackerDataConstant.NEXT_PERIOD, prediction));
    }
}