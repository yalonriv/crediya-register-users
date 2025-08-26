package com.crediya.r2dbc;

import com.crediya.model.user.User;
import com.crediya.model.user.exceptions.DomainException;
import com.crediya.r2dbc.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @InjectMocks
    MyReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    MyReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustSaveUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Juan");

        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setFirstName("Juan");

        when(mapper.map(user, UserEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.saveUser(user);

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getFirstName().equals("Juan"))
                .verifyComplete();
    }

    @Test
    void mustGetAllUsers() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setFirstName("Maria");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Maria");

        when(repository.findAll()).thenReturn(Flux.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        Flux<User> result = repositoryAdapter.getAllUsers();

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getFirstName().equals("Maria"))
                .verifyComplete();
    }

    @Test
    void mustGetUserByEmail() {
        UserEntity entity = new UserEntity();
        entity.setId(2L);
        entity.setEmail("test@mail.com");

        User user = new User();
        user.setId(2L);
        user.setEmail("test@mail.com");

        when(repository.getUserByEmail("test@mail.com")).thenReturn(Mono.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.getUserByEmail("test@mail.com");

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().equals("test@mail.com"))
                .verifyComplete();
    }

    @Test
    void mustEditUserSuccessfully() {
        // Usuario actual en la base
        UserEntity existingEntity = new UserEntity();
        existingEntity.setId(1L);
        existingEntity.setDniNumber(1111L);
        existingEntity.setFirstName("Juan");

        // Datos actualizados
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setDniNumber(1111L);
        updatedUser.setFirstName("Juan Updated");
        updatedUser.setEmail("test@test.com"); // <-- agregado

        UserEntity updatedEntity = new UserEntity();
        updatedEntity.setId(1L);
        updatedEntity.setDniNumber(1111L);
        updatedEntity.setFirstName("Juan Updated");
        updatedEntity.setEmail("test@test.com");

        // Mock repositorio
        when(repository.findById(1L)).thenReturn(Mono.just(existingEntity));
        when(repository.findByDniNumber(1111L)).thenReturn(Mono.just(existingEntity));
        when(repository.getUserByEmail("test@test.com")).thenReturn(Mono.empty()); // <-- agregado
        when(repository.save(existingEntity)).thenReturn(Mono.just(updatedEntity));
        when(mapper.map(updatedEntity, User.class)).thenReturn(updatedUser);

        Mono<User> result = repositoryAdapter.editUser(updatedUser);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getFirstName().equals("Juan Updated"))
                .verifyComplete();
    }



    @Test
    void mustFailWhenDniAlreadyExists() {
        // arrange
        UserEntity existingEntity = new UserEntity();
        existingEntity.setId(2L); // <- distinto id al del user que edito
        existingEntity.setDniNumber(12345678L);
        existingEntity.setEmail("other@test.com");

        when(repository.findById(1L)).thenReturn(Mono.just(existingEntity));
        when(repository.findByDniNumber(12345678L)).thenReturn(Mono.just(existingEntity));
        when(repository.getUserByEmail("test@test.com")).thenReturn(Mono.empty());

        User userToEdit = new User(
                1L, "Pepe", "García", null, null, null, null, null, "test@test.com", 1000.0, 12345678L
        );

        // act & assert
        StepVerifier.create(repositoryAdapter.editUser(userToEdit))
                .expectErrorMatches(ex -> ex instanceof DomainException &&
                        ex.getMessage().equals("El DNI ya está registrado"))
                .verify();
    }




}
