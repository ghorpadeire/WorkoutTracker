package com.workout.tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private int totalWorkoutsInPeriod;
    private int completedWorkoutsInPeriod;
    private List<WeeklyProgress> weeklyProgress;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyProgress {
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private int workoutsCompleted;
        private int workoutsScheduled;
    }
}
