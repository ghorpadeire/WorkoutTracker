package com.workout.tracker.dto.response;

import com.workout.tracker.entity.Exercise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {

    private Long id;
    private String name;
    private String description;
    private String category;
    private String muscleGroup;

    public static ExerciseResponse fromEntity(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .category(exercise.getCategory().name())
                .muscleGroup(exercise.getMuscleGroup() != null ? exercise.getMuscleGroup().name() : null)
                .build();
    }
}
