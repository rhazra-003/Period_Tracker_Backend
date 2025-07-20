package com.project.periodtracker.model;

import java.time.Instant;

import com.project.periodtracker.constant.TrackerDataConstant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = TrackerDataConstant.TBL_USERS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(length = 255)
    private String email;

    private String fullName;

    private Instant createdAt = Instant.now();
}