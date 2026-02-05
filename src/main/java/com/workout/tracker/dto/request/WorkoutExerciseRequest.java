package com.workout.tracker.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseRequest {

    @NotNull(message = "Exercise ID is required")
    private Long exerciseId;

    @NotNull(message = "Number of sets is required")
    @Min(value = 1, message = "Sets must be at least 1")
    private Integer sets;

    @NotNull(message = "Number of reps is required")
    @Min(value = 1, message = "Reps must be at least 1")
    private Integer reps;

    @Min(value = 0, message = "Weight cannot be negative")
    private BigDecimal weight;

    private Integer orderIndex;
}
