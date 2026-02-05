package com.workout.tracker.service;

import com.workout.tracker.dto.response.ExerciseResponse;
import com.workout.tracker.entity.Exercise;
import com.workout.tracker.entity.Exercise.ExerciseCategory;
import com.workout.tracker.entity.Exercise.MuscleGroup;
import com.workout.tracker.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Transactional(readOnly = true)
    public List<ExerciseResponse> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(ExerciseResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExerciseResponse getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + id));
        return ExerciseResponse.fromEntity(exercise);
    }

    @Transactional(readOnly = true)
    public List<ExerciseResponse> getExercisesByCategory(String category) {
        ExerciseCategory exerciseCategory = ExerciseCategory.valueOf(category.toUpperCase());
        return exerciseRepository.findByCategory(exerciseCategory).stream()
                .map(ExerciseResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExerciseResponse> getExercisesByMuscleGroup(String muscleGroup) {
        MuscleGroup group = MuscleGroup.valueOf(muscleGroup.toUpperCase());
        return exerciseRepository.findByMuscleGroup(group).stream()
                .map(ExerciseResponse::fromEntity)
                .toList();
    }
}
