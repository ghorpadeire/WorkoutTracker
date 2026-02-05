package com.workout.tracker.dto.request;

import com.workout.tracker.entity.Workout.WorkoutStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkoutRequest {

    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    private String description;

    private LocalDateTime scheduledAt;

    private WorkoutStatus status;

    private String comments;

    @Valid
    private List<WorkoutExerciseRequest> exercises;
}
