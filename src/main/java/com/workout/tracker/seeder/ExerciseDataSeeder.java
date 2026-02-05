package com.workout.tracker.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workout.tracker.entity.Exercise;
import com.workout.tracker.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExerciseDataSeeder implements CommandLineRunner {

    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (exerciseRepository.count() == 0) {
            seedExercises();
        } else {
            log.info("Exercises already seeded. Skipping...");
        }
    }

    private void seedExercises() {
        try {
            ClassPathResource resource = new ClassPathResource("data/exercises.json");
            InputStream inputStream = resource.getInputStream();

            List<ExerciseData> exerciseDataList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<ExerciseData>>() {}
            );

            List<Exercise> exercises = exerciseDataList.stream()
                    .map(this::mapToExercise)
                    .toList();

            exerciseRepository.saveAll(exercises);
            log.info("Successfully seeded {} exercises", exercises.size());

        } catch (Exception e) {
            log.error("Failed to seed exercises: {}", e.getMessage());
            throw new RuntimeException("Failed to seed exercises", e);
        }
    }

    private Exercise mapToExercise(ExerciseData data) {
        return Exercise.builder()
                .name(data.name())
                .description(data.description())
                .category(Exercise.ExerciseCategory.valueOf(data.category()))
                .muscleGroup(Exercise.MuscleGroup.valueOf(data.muscleGroup()))
                .build();
    }

    private record ExerciseData(
            String name,
            String description,
            String category,
            String muscleGroup
    ) {}
}
