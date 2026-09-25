package com.projectname.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.projectname.application.port.out.UserPort;
import com.projectname.domain.entity.User;
import com.projectname.domain.exception.ResourceNotFoundException;
import com.projectname.domain.value.UserId;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for user commands and queries.
 */
@ExtendWith(MockitoExtension.class)
class UserUseCasesTest {
    @Mock
    private UserPort userPort;

    @Test
    void shouldClearClientIdAndSetTimestampsWhenCreatingUser() {
        // Given
        final var user = User.builder()
                .id(UserId.of(123L))
                .username("alice")
                .email("alice@example.com")
                .build();
        when(userPort.save(user)).thenReturn(user);

        // When
        final var created = new CreateUserCommandImpl(userPort).invoke(user);

        // Then
        assertSame(user, created);
        assertNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertEquals(created.getCreatedAt(), created.getUpdatedAt());
        verify(userPort).save(user);
    }

    @Test
    void shouldApplyPartialUpdateWithoutReplacingExistingFields() {
        // Given
        final var id = UserId.of(1L);
        final var createdAt = OffsetDateTime.parse("2026-01-01T00:00:00Z");
        final var existing = User.builder()
                .id(id)
                .username("alice")
                .email("alice@example.com")
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
        final var updates = User.builder().email("new@example.com").build();
        when(userPort.findById(id)).thenReturn(Optional.of(existing));
        when(userPort.save(existing)).thenReturn(existing);

        // When
        final var updated = new UpdateUserCommandImpl(userPort).invoke(id, updates);

        // Then
        assertSame(existing, updated);
        assertEquals("alice", updated.getUsername());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals(createdAt, updated.getCreatedAt());
        verify(userPort).save(existing);
    }

    @Test
    void shouldRejectUpdateWhenUserDoesNotExist() {
        // Given
        final var id = UserId.of(99L);
        when(userPort.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> new UpdateUserCommandImpl(userPort)
                        .invoke(id, User.builder().username("alice").build()));
    }

    @Test
    void shouldRejectDeleteWhenUserDoesNotExist() {
        // Given
        final var id = UserId.of(99L);
        when(userPort.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> new DeleteUserCommandImpl(userPort).invoke(id));
        verify(userPort, never()).deleteById(id);
    }

    @Test
    void shouldDeleteExistingUser() {
        // Given
        final var id = UserId.of(1L);
        when(userPort.findById(id)).thenReturn(Optional.of(User.builder().id(id).build()));

        // When
        new DeleteUserCommandImpl(userPort).invoke(id);

        // Then
        verify(userPort).deleteById(id);
    }

    @Test
    void shouldReadUsersWithoutWriting() {
        // Given
        final var id = UserId.of(1L);
        final var user = User.builder().id(id).username("alice").build();
        when(userPort.findById(id)).thenReturn(Optional.of(user));
        when(userPort.findAll()).thenReturn(List.of(user));

        // When
        final var loaded = new GetUserByIdQueryImpl(userPort).invoke(id);
        final var listed = new ListUsersQueryImpl(userPort).invoke();

        // Then
        assertSame(user, loaded);
        assertEquals(List.of(user), listed);
    }
}
