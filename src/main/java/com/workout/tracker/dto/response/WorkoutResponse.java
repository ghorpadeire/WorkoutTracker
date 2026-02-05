package com.workout.tracker.dto.response;

import com.workout.tracker.entity.Workout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime scheduledAt;
    private String status;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WorkoutExerciseResponse> exercises;

    public static WorkoutResponse fromEntity(Workout workout) {
        List<WorkoutExerciseResponse> exerciseResponses = workout.getWorkoutExercises().stream()
                .map(WorkoutExerciseResponse::fromEntity)
                .toList();

        return WorkoutResponse.builder()
                .id(workout.getId())
                .title(workout.getTitle())
                .description(workout.getDescription())
                .scheduledAt(workout.getScheduledAt())
                .status(workout.getStatus().name())
                .comments(workout.getComments())
                .createdAt(workout.getCreatedAt())
                .updatedAt(workout.getUpdatedAt())
                .exercises(exerciseResponses)
                .build();
    }
}
