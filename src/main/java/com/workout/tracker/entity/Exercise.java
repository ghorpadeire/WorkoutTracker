package com.workout.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ExerciseCategory category;

    @Column(name = "muscle_group", length = 50)
    @Enumerated(EnumType.STRING)
    private MuscleGroup muscleGroup;

    public enum ExerciseCategory {
        CARDIO,
        STRENGTH,
        FLEXIBILITY
    }

    public enum MuscleGroup {
        CHEST,
        BACK,
        LEGS,
        ARMS,
        SHOULDERS,
        CORE,
        FULL_BODY
    }
}
