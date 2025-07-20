package com.project.periodtracker.exception;

public class NoCycleDataException extends RuntimeException {
    public NoCycleDataException(String message) {
        super(message);
    }
}