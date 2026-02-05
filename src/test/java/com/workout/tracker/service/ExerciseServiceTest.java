package com.workout.tracker.service;

import com.workout.tracker.dto.response.ExerciseResponse;
import com.workout.tracker.entity.Exercise;
import com.workout.tracker.entity.Exercise.ExerciseCategory;
import com.workout.tracker.entity.Exercise.MuscleGroup;
import com.workout.tracker.repository.ExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise1;
    private Exercise exercise2;

    @BeforeEach
    void setUp() {
        exercise1 = Exercise.builder()
                .id(1L)
                .name("Bench Press")
                .description("Compound chest exercise")
                .category(ExerciseCategory.STRENGTH)
                .muscleGroup(MuscleGroup.CHEST)
                .build();

        exercise2 = Exercise.builder()
                .id(2L)
                .name("Running")
                .description("Cardio exercise")
                .category(ExerciseCategory.CARDIO)
                .muscleGroup(MuscleGroup.LEGS)
                .build();
    }

    @Test
    @DisplayName("Get all exercises should return list of exercises")
    void getAllExercises_ShouldReturnAllExercises() {
        when(exerciseRepository.findAll()).thenReturn(Arrays.asList(exercise1, exercise2));

        List<ExerciseResponse> result = exerciseService.getAllExercises();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Bench Press");
        assertThat(result.get(1).getName()).isEqualTo("Running");
    }

    @Test
    @DisplayName("Get exercise by ID should return exercise when found")
    void getExerciseById_WhenFound_ShouldReturnExercise() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise1));

        ExerciseResponse result = exerciseService.getExerciseById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Bench Press");
        assertThat(result.getCategory()).isEqualTo("STRENGTH");
    }

    @Test
    @DisplayName("Get exercise by ID should throw exception when not found")
    void getExerciseById_WhenNotFound_ShouldThrowException() {
        when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseService.getExerciseById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Exercise not found");
    }

    @Test
    @DisplayName("Get exercises by category should return filtered list")
    void getExercisesByCategory_ShouldReturnFilteredList() {
        when(exerciseRepository.findByCategory(ExerciseCategory.STRENGTH))
                .thenReturn(List.of(exercise1));

        List<ExerciseResponse> result = exerciseService.getExercisesByCategory("STRENGTH");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("STRENGTH");
    }

    @Test
    @DisplayName("Get exercises by muscle group should return filtered list")
    void getExercisesByMuscleGroup_ShouldReturnFilteredList() {
        when(exerciseRepository.findByMuscleGroup(MuscleGroup.CHEST))
                .thenReturn(List.of(exercise1));

        List<ExerciseResponse> result = exerciseService.getExercisesByMuscleGroup("CHEST");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMuscleGroup()).isEqualTo("CHEST");
    }
}
