package com.workout.tracker.repository;

import com.workout.tracker.entity.Exercise;
import com.workout.tracker.entity.User;
import com.workout.tracker.entity.Workout;
import com.workout.tracker.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {

    List<WorkoutExercise> findByWorkoutOrderByOrderIndexAsc(Workout workout);

    void deleteByWorkout(Workout workout);

    @Query("SELECT we FROM WorkoutExercise we WHERE we.workout.user = :user AND we.exercise = :exercise ORDER BY we.workout.scheduledAt DESC")
    List<WorkoutExercise> findByUserAndExercise(@Param("user") User user, @Param("exercise") Exercise exercise);

    @Query("SELECT COUNT(we) FROM WorkoutExercise we WHERE we.workout.user = :user AND we.exercise.id = :exerciseId")
    long countByUserAndExerciseId(@Param("user") User user, @Param("exerciseId") Long exerciseId);
}
