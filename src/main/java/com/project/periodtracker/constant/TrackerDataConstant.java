package com.project.periodtracker.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TrackerDataConstant {
    
    public static final String BAD_REQUEST = "Bad Request!";
    public static final String INVAID_PARAMETER = "Invalid Parameter!";
    public static final String NOT_FOUND = "Not Found!";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error!";
    public static final String CYCLE_TRACKED = "Your cycle is tracked!";
    public static final String CYCLE_DELETED = "Cycle entry deleted successfully!";
    public static final String NEXT_PERIOD = "nextPeriod";
    public static final String OVULATION_DATE = "ovulationDate";
    public static final String FERTILE_WINDOW_START = "fertileWindowStart";
    public static final String FERTILE_WINDOW_END = "fertileWindowEnd";
    public static final String NAME = "Name";
    public static final String EMAIL = "email";
    public static final String USER_ID = "user_id";
    public static final String TBL_CYCLES = "cycles";
    public static final String TBL_USERS = "users";
    public static final String INVALID_PERIOD_START_DATE = "Invalid period start date.";
    public static final String INVALID_PERIOD_DURATION = "Period duration must be greater than 0.";
    public static final String INVALID_PAGINATION_PARAMETERS = "Invalid pagination parameters.";
    public static final String INVALID_MONTH = "Month must use YYYY-MM format.";
    public static final String NOT_ENOUGH_DATA_TO_PREDICT = "Record at least the last 3 period dates to unlock predictions.";
    public static final String INVALID_EMAIL = "Email is required";
    public static final String NO_CYCLE_DATA_FOUND = "No cycle data found. Please start tracking your period first!";
    public static final String INVALID_OR_EXPIRED_TOKEN = "Invalid or expired Firebase ID token.";
        
}
