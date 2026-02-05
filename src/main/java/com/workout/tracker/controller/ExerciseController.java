package com.workout.tracker.controller;

import com.workout.tracker.dto.response.ExerciseResponse;
import com.workout.tracker.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Exercise catalog management - publicly accessible")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "Get all exercises", description = "Retrieves the complete list of available exercises")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved exercise list")
    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getAllExercises() {
        List<ExerciseResponse> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    @Operation(summary = "Get exercise by ID", description = "Retrieves a specific exercise by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercise"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getExerciseById(
            @Parameter(description = "Exercise ID") @PathVariable Long id) {
        ExerciseResponse exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }

    @Operation(summary = "Get exercises by category", description = "Retrieves exercises filtered by category (CARDIO, STRENGTH, FLEXIBILITY)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises"),
            @ApiResponse(responseCode = "400", description = "Invalid category")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExerciseResponse>> getExercisesByCategory(
            @Parameter(description = "Exercise category (CARDIO, STRENGTH, FLEXIBILITY)") @PathVariable String category) {
        List<ExerciseResponse> exercises = exerciseService.getExercisesByCategory(category);
        return ResponseEntity.ok(exercises);
    }

    @Operation(summary = "Get exercises by muscle group", description = "Retrieves exercises filtered by muscle group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises"),
            @ApiResponse(responseCode = "400", description = "Invalid muscle group")
    })
    @GetMapping("/muscle-group/{muscleGroup}")
    public ResponseEntity<List<ExerciseResponse>> getExercisesByMuscleGroup(
            @Parameter(description = "Muscle group (CHEST, BACK, LEGS, ARMS, SHOULDERS, CORE, FULL_BODY)") @PathVariable String muscleGroup) {
        List<ExerciseResponse> exercises = exerciseService.getExercisesByMuscleGroup(muscleGroup);
        return ResponseEntity.ok(exercises);
    }
}
