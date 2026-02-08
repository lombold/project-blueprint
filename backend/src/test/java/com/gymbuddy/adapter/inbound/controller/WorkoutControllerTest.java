package com.gymbuddy.adapter.inbound.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.gymbuddy.adapter.inbound.controller.dto.WorkoutDTO;
import com.gymbuddy.adapter.inbound.controller.mapper.WorkoutMapper;
import com.gymbuddy.application.service.WorkoutService;
import com.gymbuddy.domain.entity.Workout;
import com.gymbuddy.domain.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.gymbuddy.domain.value.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WorkoutController.
 * Tests the REST API endpoints for workout management.
 */
@ExtendWith(MockitoExtension.class)
class WorkoutControllerTest {

  @Mock
  private WorkoutService workoutService;

  @Mock
  private WorkoutMapper workoutMapper;

  @InjectMocks
  private WorkoutController workoutController;

  private Workout workout1;
  private Workout workout2;
  private WorkoutDTO workoutDTO1;
  private WorkoutDTO workoutDTO2;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();

    // Setup domain workouts
    workout1 = Workout.builder()
        .id(1L)
        .userId(UserId.of(1L))
        .name("Push Day")
        .description("Chest, shoulders, triceps")
        .durationMinutes(60)
        .exerciseCount(4)
        .createdAt(now)
        .updatedAt(now)
        .build();

    workout2 = Workout.builder()
        .id(2L)
        .userId(UserId.of(1L))
        .name("Pull Day")
        .description("Back, biceps")
        .durationMinutes(45)
        .exerciseCount(3)
        .createdAt(now)
        .updatedAt(now)
        .build();

    // Setup DTOs
    workoutDTO1 = WorkoutDTO.builder()
        .id(1L)
        .userId(1L)
        .name("Push Day")
        .description("Chest, shoulders, triceps")
        .durationMinutes(60)
        .exerciseCount(4)
        .createdAt(now)
        .updatedAt(now)
        .build();

    workoutDTO2 = WorkoutDTO.builder()
        .id(2L)
        .userId(1L)
        .name("Pull Day")
        .description("Back, biceps")
        .durationMinutes(45)
        .exerciseCount(3)
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  @Test
  void shouldGetAllWorkouts() {
    // Given
    List<Workout> workouts = Arrays.asList(workout1, workout2);
    when(workoutService.getAllWorkouts()).thenReturn(workouts);
    when(workoutMapper.toDto(workout1)).thenReturn(workoutDTO1);
    when(workoutMapper.toDto(workout2)).thenReturn(workoutDTO2);

    // When
    ResponseEntity<List<WorkoutDTO>> response = workoutController.getAllWorkouts();

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals("Push Day", response.getBody().get(0).getName());
    assertEquals("Pull Day", response.getBody().get(1).getName());
  }

  @Test
  void shouldGetWorkoutById() {
    // Given
    when(workoutService.getWorkoutById(1L)).thenReturn(workout1);
    when(workoutMapper.toDto(workout1)).thenReturn(workoutDTO1);

    // When
    ResponseEntity<WorkoutDTO> response = workoutController.getWorkoutById(1L);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
    assertEquals("Push Day", response.getBody().getName());
    assertEquals(1L, response.getBody().getUserId());
  }

  @Test
  void shouldGetWorkoutsByUserId() {
    // Given
    List<Workout> userWorkouts = Arrays.asList(workout1, workout2);
    when(workoutService.getWorkoutsByUserId(1L)).thenReturn(userWorkouts);
    when(workoutMapper.toDto(workout1)).thenReturn(workoutDTO1);
    when(workoutMapper.toDto(workout2)).thenReturn(workoutDTO2);

    // When
    ResponseEntity<List<WorkoutDTO>> response = workoutController.getWorkoutsByUserId(1L);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals(1L, response.getBody().get(0).getUserId());
    assertEquals(1L, response.getBody().get(1).getUserId());
  }

  @Test
  void shouldReturn404WhenWorkoutNotFound() {
    // Given
    when(workoutService.getWorkoutById(999L))
        .thenThrow(new ResourceNotFoundException("Workout not found with ID: 999"));

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> {
      workoutController.getWorkoutById(999L);
    });
  }

  @Test
  void shouldCreateWorkout() {
    // Given
    WorkoutDTO createDTO = WorkoutDTO.builder()
        .userId(1L)
        .name("Leg Day")
        .description("Squats, lunges")
        .durationMinutes(50)
        .build();

    Workout createWorkout = Workout.builder()
        .userId(UserId.of(1L))
        .name("Leg Day")
        .description("Squats, lunges")
        .durationMinutes(50)
        .build();

    LocalDateTime now = LocalDateTime.now();
    Workout createdWorkout = Workout.builder()
        .id(3L)
        .userId(UserId.of(1L))
        .name("Leg Day")
        .description("Squats, lunges")
        .durationMinutes(50)
        .exerciseCount(0)
        .createdAt(now)
        .updatedAt(now)
        .build();

    WorkoutDTO createdDTO = WorkoutDTO.builder()
        .id(3L)
        .userId(1L)
        .name("Leg Day")
        .description("Squats, lunges")
        .durationMinutes(50)
        .exerciseCount(0)
        .createdAt(now)
        .updatedAt(now)
        .build();

    when(workoutMapper.toDomain(any(WorkoutDTO.class))).thenReturn(createWorkout);
    when(workoutService.createWorkout(any(Workout.class))).thenReturn(createdWorkout);
    when(workoutMapper.toDto(createdWorkout)).thenReturn(createdDTO);

    // When
    ResponseEntity<WorkoutDTO> response = workoutController.createWorkout(createDTO);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(3L, response.getBody().getId());
    assertEquals("Leg Day", response.getBody().getName());
    assertEquals(1L, response.getBody().getUserId());
  }

  @Test
  void shouldUpdateWorkout() {
    // Given
    WorkoutDTO updateDTO = WorkoutDTO.builder()
        .name("Updated Push Day")
        .description("Updated description")
        .durationMinutes(75)
        .build();

    Workout updateWorkout = Workout.builder()
        .name("Updated Push Day")
        .description("Updated description")
        .durationMinutes(75)
        .build();

    LocalDateTime createdAt = workout1.getCreatedAt();
    LocalDateTime updatedAt = LocalDateTime.now();
    Workout updatedWorkout = Workout.builder()
        .id(1L)
        .userId(UserId.of(1L))
        .name("Updated Push Day")
        .description("Updated description")
        .durationMinutes(75)
        .exerciseCount(4)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    WorkoutDTO updatedDTO = WorkoutDTO.builder()
        .id(1L)
        .userId(1L)
        .name("Updated Push Day")
        .description("Updated description")
        .durationMinutes(75)
        .exerciseCount(4)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    when(workoutMapper.toDomain(any(WorkoutDTO.class))).thenReturn(updateWorkout);
    when(workoutService.updateWorkout(anyLong(), any(Workout.class))).thenReturn(updatedWorkout);
    when(workoutMapper.toDto(updatedWorkout)).thenReturn(updatedDTO);

    // When
    ResponseEntity<WorkoutDTO> response = workoutController.updateWorkout(1L, updateDTO);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
    assertEquals("Updated Push Day", response.getBody().getName());
    assertEquals(75, response.getBody().getDurationMinutes());
  }

  @Test
  void shouldDeleteWorkout() {
    // Given
    doNothing().when(workoutService).deleteWorkout(1L);

    // When
    ResponseEntity<Void> response = workoutController.deleteWorkout(1L);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentWorkout() {
    // Given
    when(workoutService.getWorkoutById(999L))
        .thenThrow(new ResourceNotFoundException("Workout not found with ID: 999"));

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> {
      workoutService.getWorkoutById(999L);
    });
  }
}

