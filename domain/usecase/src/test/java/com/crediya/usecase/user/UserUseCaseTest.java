package com.crediya.usecase.user;

import com.crediya.model.user.User;
import com.crediya.model.user.exceptions.ValidationException;
import com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validUser = User.builder()
                .id(1L)
                .firstName("Felipe")
                .email("felipe@test.com")
                .dniNumber(123456L)
                .birthDate(LocalDate.now().minusYears(20)) // mayor de edad
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.getUserByDniNumber(validUser.getDniNumber())).thenReturn(Mono.empty());
        when(userRepository.getUserByEmail(validUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.saveUser(validUser)).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.createUser(validUser))
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).saveUser(validUser);
    }

    @Test
    void shouldFailWhenUserIsUnder18() {
        User minorUser = User.builder()
                .id(2L)
                .firstName("Junior")
                .email("junior@test.com")
                .dniNumber(654321L)
                .birthDate(LocalDate.now().minusYears(15)) // menor de edad
                .build();

        StepVerifier.create(userUseCase.createUser(minorUser))
                .expectErrorMatches(ex -> ex instanceof ValidationException &&
                        ex.getMessage().equals("El usuario debe ser mayor de edad"))
                .verify();

        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void shouldFailWhenDniAlreadyExists() {
        when(userRepository.getUserByDniNumber(validUser.getDniNumber()))
                .thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.createUser(validUser))
                .expectErrorMatches(ex -> ex instanceof ValidationException &&
                        ex.getMessage().equals("Ya existe un usuario con el mismo DNI"))
                .verify();

        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        when(userRepository.getUserByDniNumber(validUser.getDniNumber())).thenReturn(Mono.empty());
        when(userRepository.getUserByEmail(validUser.getEmail())).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.createUser(validUser))
                .expectErrorMatches(ex -> ex instanceof ValidationException &&
                        ex.getMessage().equals("Ya existe un usuario con el mismo email"))
                .verify();

        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void shouldListUsers() {
        when(userRepository.getAllUsers()).thenReturn(Flux.just(validUser));

        StepVerifier.create(userUseCase.listUsers())
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void shouldGetUserByDni() {
        when(userRepository.getUserByDniNumber(123456L)).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.getUserByDni(123456L))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.editUser(validUser)).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.updateUser(validUser))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.deleteUser(1L)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.deleteUser(1L))
                .verifyComplete();

        verify(userRepository).deleteUser(1L);
    }
}
