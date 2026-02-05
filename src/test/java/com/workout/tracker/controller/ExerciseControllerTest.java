package com.workout.tracker.controller;

import com.workout.tracker.dto.response.ExerciseResponse;
import com.workout.tracker.service.ExerciseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseService exerciseService;

    @Test
    @DisplayName("GET /api/exercises should return all exercises")
    void getAllExercises_ShouldReturnExercisesList() throws Exception {
        List<ExerciseResponse> exercises = Arrays.asList(
                ExerciseResponse.builder()
                        .id(1L)
                        .name("Bench Press")
                        .category("STRENGTH")
                        .muscleGroup("CHEST")
                        .build(),
                ExerciseResponse.builder()
                        .id(2L)
                        .name("Running")
                        .category("CARDIO")
                        .muscleGroup("LEGS")
                        .build()
        );

        when(exerciseService.getAllExercises()).thenReturn(exercises);

        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Bench Press"))
                .andExpect(jsonPath("$[1].name").value("Running"));
    }

    @Test
    @DisplayName("GET /api/exercises/{id} should return specific exercise")
    void getExerciseById_WhenFound_ShouldReturnExercise() throws Exception {
        ExerciseResponse exercise = ExerciseResponse.builder()
                .id(1L)
                .name("Bench Press")
                .description("Compound chest exercise")
                .category("STRENGTH")
                .muscleGroup("CHEST")
                .build();

        when(exerciseService.getExerciseById(1L)).thenReturn(exercise);

        mockMvc.perform(get("/api/exercises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bench Press"))
                .andExpect(jsonPath("$.category").value("STRENGTH"));
    }

    @Test
    @DisplayName("GET /api/exercises/category/{category} should return filtered exercises")
    void getExercisesByCategory_ShouldReturnFilteredList() throws Exception {
        List<ExerciseResponse> exercises = List.of(
                ExerciseResponse.builder()
                        .id(1L)
                        .name("Bench Press")
                        .category("STRENGTH")
                        .muscleGroup("CHEST")
                        .build()
        );

        when(exerciseService.getExercisesByCategory("STRENGTH")).thenReturn(exercises);

        mockMvc.perform(get("/api/exercises/category/STRENGTH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("STRENGTH"));
    }

    @Test
    @DisplayName("GET /api/exercises/muscle-group/{group} should return filtered exercises")
    void getExercisesByMuscleGroup_ShouldReturnFilteredList() throws Exception {
        List<ExerciseResponse> exercises = List.of(
                ExerciseResponse.builder()
                        .id(1L)
                        .name("Bench Press")
                        .category("STRENGTH")
                        .muscleGroup("CHEST")
                        .build()
        );

        when(exerciseService.getExercisesByMuscleGroup("CHEST")).thenReturn(exercises);

        mockMvc.perform(get("/api/exercises/muscle-group/CHEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].muscleGroup").value("CHEST"));
    }
}
