package com.workout.tracker.service;

import com.workout.tracker.dto.request.CreateWorkoutRequest;
import com.workout.tracker.dto.request.UpdateWorkoutRequest;
import com.workout.tracker.dto.request.WorkoutExerciseRequest;
import com.workout.tracker.dto.response.WorkoutResponse;
import com.workout.tracker.entity.Exercise;
import com.workout.tracker.entity.User;
import com.workout.tracker.entity.Workout;
import com.workout.tracker.entity.Workout.WorkoutStatus;
import com.workout.tracker.entity.WorkoutExercise;
import com.workout.tracker.exception.ResourceNotFoundException;
import com.workout.tracker.exception.UnauthorizedException;
import com.workout.tracker.repository.ExerciseRepository;
import com.workout.tracker.repository.WorkoutExerciseRepository;
import com.workout.tracker.repository.WorkoutRepository;
import com.workout.tracker.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public WorkoutResponse createWorkout(CreateWorkoutRequest request) {
        User user = getCurrentUser();

        Workout workout = Workout.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduledAt(request.getScheduledAt())
                .status(WorkoutStatus.PENDING)
                .workoutExercises(new ArrayList<>())
                .build();

        Workout savedWorkout = workoutRepository.save(workout);

        List<WorkoutExercise> workoutExercises = createWorkoutExercises(request.getExercises(), savedWorkout);
        savedWorkout.setWorkoutExercises(workoutExercises);

        return WorkoutResponse.fromEntity(savedWorkout);
    }

    @Transactional(readOnly = true)
    public WorkoutResponse getWorkoutById(Long id) {
        User user = getCurrentUser();
        Workout workout = workoutRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));
        return WorkoutResponse.fromEntity(workout);
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponse> getAllWorkouts(String status) {
        User user = getCurrentUser();
        List<Workout> workouts;

        if (status != null && !status.isEmpty()) {
            WorkoutStatus workoutStatus = WorkoutStatus.valueOf(status.toUpperCase());
            workouts = workoutRepository.findByUserAndStatusOrderByScheduledAtDesc(user, workoutStatus);
        } else {
            workouts = workoutRepository.findByUserOrderByScheduledAtDesc(user);
        }

        return workouts.stream()
                .map(WorkoutResponse::fromEntity)
                .toList();
    }

    @Transactional
    public WorkoutResponse updateWorkout(Long id, UpdateWorkoutRequest request) {
        User user = getCurrentUser();
        Workout workout = workoutRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));

        if (request.getTitle() != null) {
            workout.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            workout.setDescription(request.getDescription());
        }
        if (request.getScheduledAt() != null) {
            workout.setScheduledAt(request.getScheduledAt());
        }
        if (request.getStatus() != null) {
            workout.setStatus(request.getStatus());
        }
        if (request.getComments() != null) {
            workout.setComments(request.getComments());
        }

        if (request.getExercises() != null && !request.getExercises().isEmpty()) {
            workoutExerciseRepository.deleteByWorkout(workout);
            workout.getWorkoutExercises().clear();
            List<WorkoutExercise> newExercises = createWorkoutExercises(request.getExercises(), workout);
            workout.setWorkoutExercises(newExercises);
        }

        Workout updatedWorkout = workoutRepository.save(workout);
        return WorkoutResponse.fromEntity(updatedWorkout);
    }

    @Transactional
    public void deleteWorkout(Long id) {
        User user = getCurrentUser();
        Workout workout = workoutRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));
        workoutRepository.delete(workout);
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponse> getScheduledWorkouts() {
        User user = getCurrentUser();
        List<Workout> workouts = workoutRepository.findScheduledWorkouts(user, LocalDateTime.now());
        return workouts.stream()
                .map(WorkoutResponse::fromEntity)
                .toList();
    }

    private List<WorkoutExercise> createWorkoutExercises(List<WorkoutExerciseRequest> requests, Workout workout) {
        List<WorkoutExercise> workoutExercises = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            WorkoutExerciseRequest req = requests.get(i);
            Exercise exercise = exerciseRepository.findById(req.getExerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", req.getExerciseId()));

            WorkoutExercise workoutExercise = WorkoutExercise.builder()
                    .workout(workout)
                    .exercise(exercise)
                    .sets(req.getSets())
                    .reps(req.getReps())
                    .weight(req.getWeight())
                    .orderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : i)
                    .build();

            workoutExercises.add(workoutExerciseRepository.save(workoutExercise));
        }

        return workoutExercises;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDetailsService.loadUserEntityByEmail(email);
    }
}
