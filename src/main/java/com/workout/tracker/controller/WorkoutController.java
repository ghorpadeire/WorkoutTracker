package com.workout.tracker.controller;

import com.workout.tracker.dto.request.CreateWorkoutRequest;
import com.workout.tracker.dto.request.UpdateWorkoutRequest;
import com.workout.tracker.dto.response.WorkoutResponse;
import com.workout.tracker.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts", description = "Workout management endpoints - requires authentication")
@SecurityRequirement(name = "bearerAuth")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Operation(summary = "Create a new workout", description = "Creates a new workout plan with exercises for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Workout successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@Valid @RequestBody CreateWorkoutRequest request) {
        WorkoutResponse response = workoutService.createWorkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get workout by ID", description = "Retrieves a specific workout belonging to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved workout"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkoutById(
            @Parameter(description = "Workout ID") @PathVariable Long id) {
        WorkoutResponse response = workoutService.getWorkoutById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all workouts", description = "Retrieves all workouts for the authenticated user, optionally filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved workouts"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> getAllWorkouts(
            @Parameter(description = "Filter by status (PENDING, COMPLETED, CANCELLED)") @RequestParam(required = false) String status) {
        List<WorkoutResponse> workouts = workoutService.getAllWorkouts(status);
        return ResponseEntity.ok(workouts);
    }

    @Operation(summary = "Update a workout", description = "Updates an existing workout's details, exercises, or status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponse> updateWorkout(
            @Parameter(description = "Workout ID") @PathVariable Long id,
            @Valid @RequestBody UpdateWorkoutRequest request) {
        WorkoutResponse response = workoutService.updateWorkout(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a workout", description = "Permanently deletes a workout belonging to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Workout successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @Parameter(description = "Workout ID") @PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get scheduled workouts", description = "Retrieves all pending workouts scheduled for the future")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved scheduled workouts"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/scheduled")
    public ResponseEntity<List<WorkoutResponse>> getScheduledWorkouts() {
        List<WorkoutResponse> workouts = workoutService.getScheduledWorkouts();
        return ResponseEntity.ok(workouts);
    }
}
