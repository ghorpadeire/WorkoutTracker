package com.workout.tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseStatsResponse {

    private Long exerciseId;
    private String exerciseName;
    private String category;
    private String muscleGroup;
    private long timesPerformed;
    private int totalSets;
    private int totalReps;
    private BigDecimal maxWeight;
    private BigDecimal averageWeight;
}
