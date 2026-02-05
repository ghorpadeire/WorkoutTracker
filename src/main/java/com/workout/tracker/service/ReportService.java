package com.workout.tracker.service;

import com.workout.tracker.dto.response.ExerciseStatsResponse;
import com.workout.tracker.dto.response.ProgressResponse;
import com.workout.tracker.dto.response.ProgressResponse.WeeklyProgress;
import com.workout.tracker.dto.response.WorkoutSummaryResponse;
import com.workout.tracker.entity.Exercise;
import com.workout.tracker.entity.User;
import com.workout.tracker.entity.Workout;
import com.workout.tracker.entity.Workout.WorkoutStatus;
import com.workout.tracker.entity.WorkoutExercise;
import com.workout.tracker.exception.ResourceNotFoundException;
import com.workout.tracker.repository.ExerciseRepository;
import com.workout.tracker.repository.WorkoutExerciseRepository;
import com.workout.tracker.repository.WorkoutRepository;
import com.workout.tracker.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Transactional(readOnly = true)
    public WorkoutSummaryResponse getWorkoutSummary() {
        User user = getCurrentUser();

        long totalWorkouts = workoutRepository.findByUserOrderByScheduledAtDesc(user).size();
        long completedWorkouts = workoutRepository.countByUserAndStatus(user, WorkoutStatus.COMPLETED);
        long pendingWorkouts = workoutRepository.countByUserAndStatus(user, WorkoutStatus.PENDING);
        long cancelledWorkouts = workoutRepository.countByUserAndStatus(user, WorkoutStatus.CANCELLED);

        List<Workout> completedWorkoutList = workoutRepository.findCompletedWorkouts(user);
        long totalExercisesPerformed = completedWorkoutList.stream()
                .mapToLong(w -> w.getWorkoutExercises().size())
                .sum();

        double completionRate = totalWorkouts > 0
                ? (double) completedWorkouts / totalWorkouts * 100
                : 0.0;

        return WorkoutSummaryResponse.builder()
                .totalWorkouts(totalWorkouts)
                .completedWorkouts(completedWorkouts)
                .pendingWorkouts(pendingWorkouts)
                .cancelledWorkouts(cancelledWorkouts)
                .totalExercisesPerformed(totalExercisesPerformed)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .build();
    }

    @Transactional(readOnly = true)
    public ProgressResponse getProgress(Integer weeks) {
        User user = getCurrentUser();
        int weeksToAnalyze = weeks != null ? weeks : 4;

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(weeksToAnalyze);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Workout> workoutsInPeriod = workoutRepository.findByUserAndScheduledAtBetween(
                user, startDateTime, endDateTime);

        int totalWorkoutsInPeriod = workoutsInPeriod.size();
        int completedWorkoutsInPeriod = (int) workoutsInPeriod.stream()
                .filter(w -> w.getStatus() == WorkoutStatus.COMPLETED)
                .count();

        List<WeeklyProgress> weeklyProgressList = new ArrayList<>();
        LocalDate weekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        while (!weekStart.isAfter(endDate)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(endDate)) {
                weekEnd = endDate;
            }

            LocalDateTime weekStartTime = weekStart.atStartOfDay();
            LocalDateTime weekEndTime = weekEnd.atTime(LocalTime.MAX);

            List<Workout> weekWorkouts = workoutRepository.findByUserAndScheduledAtBetween(
                    user, weekStartTime, weekEndTime);

            int scheduled = weekWorkouts.size();
            int completed = (int) weekWorkouts.stream()
                    .filter(w -> w.getStatus() == WorkoutStatus.COMPLETED)
                    .count();

            weeklyProgressList.add(WeeklyProgress.builder()
                    .weekStart(weekStart)
                    .weekEnd(weekEnd)
                    .workoutsScheduled(scheduled)
                    .workoutsCompleted(completed)
                    .build());

            weekStart = weekStart.plusWeeks(1);
        }

        return ProgressResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalWorkoutsInPeriod(totalWorkoutsInPeriod)
                .completedWorkoutsInPeriod(completedWorkoutsInPeriod)
                .weeklyProgress(weeklyProgressList)
                .build();
    }

    @Transactional(readOnly = true)
    public ExerciseStatsResponse getExerciseStats(Long exerciseId) {
        User user = getCurrentUser();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", exerciseId));

        List<WorkoutExercise> workoutExercises = workoutExerciseRepository.findByUserAndExercise(user, exercise);

        if (workoutExercises.isEmpty()) {
            return ExerciseStatsResponse.builder()
                    .exerciseId(exercise.getId())
                    .exerciseName(exercise.getName())
                    .category(exercise.getCategory().name())
                    .muscleGroup(exercise.getMuscleGroup() != null ? exercise.getMuscleGroup().name() : null)
                    .timesPerformed(0)
                    .totalSets(0)
                    .totalReps(0)
                    .maxWeight(BigDecimal.ZERO)
                    .averageWeight(BigDecimal.ZERO)
                    .build();
        }

        long timesPerformed = workoutExercises.size();
        int totalSets = workoutExercises.stream().mapToInt(WorkoutExercise::getSets).sum();
        int totalReps = workoutExercises.stream()
                .mapToInt(we -> we.getSets() * we.getReps())
                .sum();

        BigDecimal maxWeight = workoutExercises.stream()
                .map(WorkoutExercise::getWeight)
                .filter(w -> w != null)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal averageWeight = workoutExercises.stream()
                .map(WorkoutExercise::getWeight)
                .filter(w -> w != null && w.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long weightCount = workoutExercises.stream()
                .filter(we -> we.getWeight() != null && we.getWeight().compareTo(BigDecimal.ZERO) > 0)
                .count();

        if (weightCount > 0) {
            averageWeight = averageWeight.divide(BigDecimal.valueOf(weightCount), 2, RoundingMode.HALF_UP);
        }

        return ExerciseStatsResponse.builder()
                .exerciseId(exercise.getId())
                .exerciseName(exercise.getName())
                .category(exercise.getCategory().name())
                .muscleGroup(exercise.getMuscleGroup() != null ? exercise.getMuscleGroup().name() : null)
                .timesPerformed(timesPerformed)
                .totalSets(totalSets)
                .totalReps(totalReps)
                .maxWeight(maxWeight)
                .averageWeight(averageWeight)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDetailsService.loadUserEntityByEmail(email);
    }
}
