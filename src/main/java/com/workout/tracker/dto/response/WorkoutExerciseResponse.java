package com.workout.tracker.dto.response;

import com.workout.tracker.entity.WorkoutExercise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseResponse {

    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private String category;
    private String muscleGroup;
    private Integer sets;
    private Integer reps;
    private BigDecimal weight;
    private Integer orderIndex;

    public static WorkoutExerciseResponse fromEntity(WorkoutExercise workoutExercise) {
        return WorkoutExerciseResponse.builder()
                .id(workoutExercise.getId())
                .exerciseId(workoutExercise.getExercise().getId())
                .exerciseName(workoutExercise.getExercise().getName())
                .category(workoutExercise.getExercise().getCategory().name())
                .muscleGroup(workoutExercise.getExercise().getMuscleGroup() != null
                        ? workoutExercise.getExercise().getMuscleGroup().name() : null)
                .sets(workoutExercise.getSets())
                .reps(workoutExercise.getReps())
                .weight(workoutExercise.getWeight())
                .orderIndex(workoutExercise.getOrderIndex())
                .build();
    }
}
