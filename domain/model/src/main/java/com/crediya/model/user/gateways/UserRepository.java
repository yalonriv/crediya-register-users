package com.crediya.model.user.gateways;

import com.crediya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Puerto secundario donde se declaran todos los
// métodos que deben ser implementados por los
// driven adapters
public interface UserRepository {
    Mono<User> saveUser(User user);
    Flux<User> getAllUsers();
    Mono<User> editUser(User user);
    Mono<Void> deleteUser(Long idNumber);
    Mono<User> getUserByDniNumber(Long dniNumber);
    Mono<User> getUserByEmail(String email);
}
