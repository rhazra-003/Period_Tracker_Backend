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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cycles")
@RequiredArgsConstructor
public class CycleController {

    private final CycleService cycleService;
    private final UserRepository userRepo;

    @PostMapping("/track")
    public ResponseEntity<?> track(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam Integer duration,
            HttpServletRequest request) {
        String email = getAuthenticatedEmail();
        if (start == null) {
            return ResponseEntity.badRequest().body(Map.of("error", TrackerDataConstant.INVALID_PERIOD_START_DATE));
        }
        if (duration == null || duration <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", TrackerDataConstant.INVALID_PERIOD_DURATION));
        }
        if (!userRepo.existsById(email)) {
            String fullName = (String) request.getAttribute("firebaseUserName");
            if (fullName == null || fullName.isBlank()) {
                fullName = TrackerDataConstant.NAME;
            }
            userRepo.save(new User(email, fullName, Instant.now()));
        }
        cycleService.trackCycle(email, start, duration);
        return ResponseEntity.ok(TrackerDataConstant.CYCLE_TRACKED);
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getCycles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = getAuthenticatedEmail();
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", TrackerDataConstant.INVALID_PAGINATION_PARAMETERS));
        }
        return ResponseEntity.ok(cycleService.getRecentCycles(email, page, size));
    }

    @GetMapping("/predict")
    public ResponseEntity<?> predict() {
        String email = getAuthenticatedEmail();
        try {
            LocalDate prediction = cycleService.predictNextPeriod(email);
            return ResponseEntity.ok(Map.of(TrackerDataConstant.NEXT_PERIOD, prediction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }
}