package com.project.periodtracker.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TrackerSQLConstant {

    public static final String FETCH_LAST_FOUR_PERIOD_DATES = """
        SELECT c.periodStartDate FROM CycleEntry c
        WHERE c.user.email = :email
        ORDER BY c.periodStartDate DESC
        LIMIT 4
        """;
}
