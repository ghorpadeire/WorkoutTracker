package com.workout.tracker.controller;

import com.workout.tracker.dto.response.ExerciseStatsResponse;
import com.workout.tracker.dto.response.ProgressResponse;
import com.workout.tracker.dto.response.WorkoutSummaryResponse;
import com.workout.tracker.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Workout statistics and progress tracking - requires authentication")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get workout summary", description = "Retrieves overall workout statistics including totals and completion rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved summary"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/summary")
    public ResponseEntity<WorkoutSummaryResponse> getWorkoutSummary() {
        WorkoutSummaryResponse summary = reportService.getWorkoutSummary();
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Get progress report", description = "Retrieves weekly workout progress over a specified number of weeks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved progress report"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getProgress(
            @Parameter(description = "Number of weeks to analyze (default: 4)") @RequestParam(required = false, defaultValue = "4") Integer weeks) {
        ProgressResponse progress = reportService.getProgress(weeks);
        return ResponseEntity.ok(progress);
    }

    @Operation(summary = "Get exercise statistics", description = "Retrieves performance statistics for a specific exercise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercise stats"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @GetMapping("/exercises/{exerciseId}")
    public ResponseEntity<ExerciseStatsResponse> getExerciseStats(
            @Parameter(description = "Exercise ID") @PathVariable Long exerciseId) {
        ExerciseStatsResponse stats = reportService.getExerciseStats(exerciseId);
        return ResponseEntity.ok(stats);
    }
}
