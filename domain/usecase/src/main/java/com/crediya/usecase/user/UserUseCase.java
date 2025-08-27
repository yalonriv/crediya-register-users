package com.crediya.usecase.user;

import com.crediya.model.user.User;
import com.crediya.model.user.exceptions.ValidationException;
import com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        return validateAge(user)
                .then(Mono.defer(() -> validateUniqueDni(user.getDniNumber())))
                .then(Mono.defer(() -> validateUniqueEmail(user.getEmail())))
                .then(Mono.defer(() -> userRepository.saveUser(user)));
    }

    private Mono<Void> validateAge(User user) {
        return Mono.fromCallable(() -> {
            LocalDate currentDate = LocalDate.now();
            Period age = Period.between(user.getBirthDate(), currentDate);
            if (age.getYears() < 18) {
                throw new ValidationException("El usuario debe ser mayor de edad");
            }
            return null;
        });
    }

    private Mono<Void> validateUniqueDni(Long dniNumber) {
        return userRepository.getUserByDniNumber(dniNumber)  // ¡Usa el nuevo método!
                .flatMap(existingUser ->
                        Mono.error(new ValidationException("Ya existe un usuario con el mismo DNI"))
                )
                .then();
    }

    private Mono<Void> validateUniqueEmail(String email) {
        return userRepository.getUserByEmail(email)
                .flatMap(existingUser ->
                        Mono.error(new ValidationException("Ya existe un usuario con el mismo email")))
                .then();
    }

    public Flux<User> listUsers() {
        return userRepository.getAllUsers();
    }

    public Mono<User> getUserByDni(Long dniNumber) {
        return userRepository.getUserByDniNumber(dniNumber);
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public Mono<User> updateUser(User user) {
        // Aquí también deberías agregar validaciones similares, pero teniendo en cuenta que el usuario actual no debe ser considerado en las validaciones de unicidad.
        return userRepository.editUser(user);
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteUser(id);
    }
}