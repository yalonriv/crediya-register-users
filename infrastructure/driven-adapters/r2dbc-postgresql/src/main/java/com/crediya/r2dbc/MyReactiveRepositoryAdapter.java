package com.crediya.r2dbc;

import com.crediya.model.user.User;
import com.crediya.model.user.exceptions.DomainException;
import com.crediya.model.user.exceptions.ValidationException;
import com.crediya.model.user.gateways.UserRepository;
import com.crediya.r2dbc.entities.UserEntity;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, Long, MyReactiveRepository>
        implements UserRepository {

    private final MyReactiveRepository repository;
    private final ObjectMapper mapper;


    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<User> saveUser(User user) {
        UserEntity entity = mapper.map(user, UserEntity.class);
        return repository.save(entity)   // guarda en BD
                .map(e -> mapper.map(e, User.class)); // regresa al dominio
    }

    @Override
    public Flux<User> getAllUsers() {
        return repository.findAll()
                .map(e -> mapper.map(e, User.class));
    }

    @Override
    public Mono<User> editUser(User user) {
        return repository.findById(user.getId())
                .switchIfEmpty(Mono.error(new ValidationException("Usuario no encontrado")))
                .flatMap(existingEntity -> {
                    // 1. Si el DNI no cambió, saltar validación
                    if (existingEntity.getDniNumber().equals(user.getDniNumber())) {
                        System.out.println("DEBUG: DNI no cambió, saltando validación");
                        return validateEmailUniqueness(user, existingEntity);
                    }

                    // 2. Solo validar DNI si realmente cambió
                    return repository.findByDniNumber(user.getDniNumber())
                            .flatMap(otherUser -> {
                                System.out.println("DEBUG: DNI cambiado, validando...");
                                return Mono.error(new ValidationException("El DNI ya esta registrado por otro usuario"));
                            })
                            .switchIfEmpty(validateEmailUniqueness(user, existingEntity))
                            .cast(UserEntity.class);
                })
                .map(entity -> mapper.map(entity, User.class));
    }


    private Mono<UserEntity> validateEmailUniqueness(User user, UserEntity existingEntity) {
        return repository.getUserByEmail(user.getEmail())
                .flatMap(otherUser -> {
                    if (!otherUser.getId().equals(user.getId())) {
                        return Mono.error(new ValidationException("El email ya está registrado por otro usuario"));
                    }
                    // Si es el mismo usuario, actualizar
                    return updateAndSaveUser(user, existingEntity);
                })
                .switchIfEmpty(updateAndSaveUser(user, existingEntity)); // Si no existe usuario con ese email
    }

    private Mono<UserEntity> updateAndSaveUser(User user, UserEntity existingEntity) {
        // Actualizar campos
        existingEntity.setFirstName(user.getFirstName());
        existingEntity.setSecondName(user.getSecondName());
        existingEntity.setSurName(user.getSurName());
        existingEntity.setSecondSurName(user.getSecondSurName());
        existingEntity.setBirthDate(user.getBirthDate());
        existingEntity.setAddress(user.getAddress());
        existingEntity.setPhoneNumber(user.getPhoneNumber());
        existingEntity.setEmail(user.getEmail());
        existingEntity.setBaseSalary(user.getBaseSalary());
        existingEntity.setDniNumber(user.getDniNumber());

        return repository.save(existingEntity);
    }

    @Override
    public Mono<Void> deleteUser(Long idNumber) {
        return repository.deleteById(idNumber);
    }

    @Override
    public Mono<User> getUserByDniNumber(Long dniNumber) {
        return repository.findByDniNumber(dniNumber)
                .map(entity -> mapper.map(entity, User.class));
    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return repository.getUserByEmail(email)
                .map(entity -> mapper.map(entity, User.class));
    }
}
