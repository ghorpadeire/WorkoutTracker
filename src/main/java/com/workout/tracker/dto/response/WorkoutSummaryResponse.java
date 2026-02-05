package com.workout.tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSummaryResponse {

    private long totalWorkouts;
    private long completedWorkouts;
    private long pendingWorkouts;
    private long cancelledWorkouts;
    private long totalExercisesPerformed;
    private double completionRate;
}
