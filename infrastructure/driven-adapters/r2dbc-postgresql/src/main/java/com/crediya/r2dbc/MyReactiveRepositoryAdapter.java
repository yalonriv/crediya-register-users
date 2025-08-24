package com.crediya.r2dbc;

import com.crediya.model.user.User;
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
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(existing ->
                        // Validar que el DNI no esté en uso por otro
                        repository.findByDniNumber(user.getDniNumber())
                                .filter(u -> !u.getId().equals(user.getId()))
                                .flatMap(u -> Mono.<UserEntity>error(new RuntimeException("El DNI ya está registrado")))
                                .switchIfEmpty(
                                        // Validar que el email no esté en uso por otro
                                        repository.getUserByEmail(user.getEmail())
                                                .filter(u -> !u.getId().equals(user.getId()))
                                                .flatMap(u -> Mono.<UserEntity>error(new RuntimeException("El email ya está registrado")))
                                )
                                .switchIfEmpty(Mono.defer(() -> {
                                    // Actualizar los campos permitidos
                                    existing.setFirstName(user.getFirstName());
                                    existing.setSecondName(user.getSecondName());
                                    existing.setSurName(user.getSurName());
                                    existing.setSecondSurName(user.getSecondSurName());
                                    existing.setBirthDate(user.getBirthDate());
                                    existing.setAddress(user.getAddress());
                                    existing.setPhoneNumber(user.getPhoneNumber());
                                    existing.setEmail(user.getEmail());
                                    existing.setBaseSalary(user.getBaseSalary());
                                    existing.setDniNumber(user.getDniNumber());

                                    return repository.save(existing);
                                }))
                )
                .map(e -> mapper.map(e, User.class));
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
