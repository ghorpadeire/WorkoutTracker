package com.workout.tracker.service;

import com.workout.tracker.dto.request.CreateWorkoutRequest;
import com.workout.tracker.dto.request.UpdateWorkoutRequest;
import com.workout.tracker.dto.request.WorkoutExerciseRequest;
import com.workout.tracker.dto.response.WorkoutResponse;
import com.workout.tracker.entity.Exercise;
import com.workout.tracker.entity.Exercise.ExerciseCategory;
import com.workout.tracker.entity.Exercise.MuscleGroup;
import com.workout.tracker.entity.User;
import com.workout.tracker.entity.Workout;
import com.workout.tracker.entity.Workout.WorkoutStatus;
import com.workout.tracker.entity.WorkoutExercise;
import com.workout.tracker.exception.ResourceNotFoundException;
import com.workout.tracker.repository.ExerciseRepository;
import com.workout.tracker.repository.WorkoutExerciseRepository;
import com.workout.tracker.repository.WorkoutRepository;
import com.workout.tracker.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private WorkoutService workoutService;

    private User user;
    private Exercise exercise;
    private Workout workout;
    private WorkoutExercise workoutExercise;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        exercise = Exercise.builder()
                .id(1L)
                .name("Bench Press")
                .category(ExerciseCategory.STRENGTH)
                .muscleGroup(MuscleGroup.CHEST)
                .build();

        workoutExercise = WorkoutExercise.builder()
                .id(1L)
                .exercise(exercise)
                .sets(3)
                .reps(10)
                .weight(BigDecimal.valueOf(60))
                .orderIndex(0)
                .build();

        workout = Workout.builder()
                .id(1L)
                .user(user)
                .title("Morning Workout")
                .description("Full body workout")
                .status(WorkoutStatus.PENDING)
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .workoutExercises(new ArrayList<>(List.of(workoutExercise)))
                .build();

        workoutExercise.setWorkout(workout);

        setupSecurityContext();
    }

    private void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john@example.com");
        SecurityContextHolder.setContext(securityContext);
        lenient().when(userDetailsService.loadUserEntityByEmail("john@example.com")).thenReturn(user);
    }

    @Test
    @DisplayName("Create workout should return created workout")
    void createWorkout_WithValidRequest_ShouldReturnWorkout() {
        CreateWorkoutRequest request = new CreateWorkoutRequest();
        request.setTitle("New Workout");
        request.setDescription("Test workout");
        request.setScheduledAt(LocalDateTime.now().plusDays(1));
        request.setExercises(List.of(
                new WorkoutExerciseRequest(1L, 3, 10, BigDecimal.valueOf(60), 0)
        ));

        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(workoutExerciseRepository.save(any(WorkoutExercise.class))).thenReturn(workoutExercise);

        WorkoutResponse response = workoutService.createWorkout(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Morning Workout");
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    @DisplayName("Get workout by ID should return workout when found")
    void getWorkoutById_WhenFound_ShouldReturnWorkout() {
        when(workoutRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(workout));

        WorkoutResponse response = workoutService.getWorkoutById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Morning Workout");
    }

    @Test
    @DisplayName("Get workout by ID should throw exception when not found")
    void getWorkoutById_WhenNotFound_ShouldThrowException() {
        when(workoutRepository.findByIdAndUser(999L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.getWorkoutById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get all workouts should return user's workouts")
    void getAllWorkouts_ShouldReturnUserWorkouts() {
        when(workoutRepository.findByUserOrderByScheduledAtDesc(user))
                .thenReturn(List.of(workout));

        List<WorkoutResponse> result = workoutService.getAllWorkouts(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Morning Workout");
    }

    @Test
    @DisplayName("Update workout should update and return workout")
    void updateWorkout_WithValidRequest_ShouldReturnUpdatedWorkout() {
        UpdateWorkoutRequest request = new UpdateWorkoutRequest();
        request.setTitle("Updated Workout");
        request.setStatus(WorkoutStatus.COMPLETED);

        when(workoutRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);

        WorkoutResponse response = workoutService.updateWorkout(1L, request);

        assertThat(response).isNotNull();
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    @DisplayName("Delete workout should remove workout")
    void deleteWorkout_WhenFound_ShouldDelete() {
        when(workoutRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(workout));
        doNothing().when(workoutRepository).delete(workout);

        workoutService.deleteWorkout(1L);

        verify(workoutRepository).delete(workout);
    }
}
