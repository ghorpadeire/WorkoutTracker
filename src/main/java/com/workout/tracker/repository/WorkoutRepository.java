package com.workout.tracker.repository;

import com.workout.tracker.entity.User;
import com.workout.tracker.entity.Workout;
import com.workout.tracker.entity.Workout.WorkoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUserOrderByScheduledAtDesc(User user);

    List<Workout> findByUserAndStatusOrderByScheduledAtDesc(User user, WorkoutStatus status);

    Optional<Workout> findByIdAndUser(Long id, User user);

    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.status = 'PENDING' AND w.scheduledAt >= :now ORDER BY w.scheduledAt ASC")
    List<Workout> findScheduledWorkouts(@Param("user") User user, @Param("now") LocalDateTime now);

    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.status = 'COMPLETED' ORDER BY w.updatedAt DESC")
    List<Workout> findCompletedWorkouts(@Param("user") User user);

    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user = :user AND w.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") WorkoutStatus status);

    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.scheduledAt BETWEEN :start AND :end ORDER BY w.scheduledAt ASC")
    List<Workout> findByUserAndScheduledAtBetween(@Param("user") User user, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
