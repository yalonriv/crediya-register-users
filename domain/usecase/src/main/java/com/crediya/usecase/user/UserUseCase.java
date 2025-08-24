package com.crediya.usecase.user;

//Se añaden los métodos que van a ser expuestos
// Y orquestan todo el flujo de lo que estemos
// necesitando
import com.crediya.model.user.User;
import com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

//HAY QUE HACER EL PUERTO PRIMARIO, LA INTERFAZ DONDE
//SE PONGAN LAS FIRMS Y ESTA CLASE LO IMPLEMENTE
@RequiredArgsConstructor
public class UserUseCase {
    //AQUI SE DEBEN HACER LAS VALIDACIONES
    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        return userRepository.saveUser(user);
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
        return userRepository.editUser(user);
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteUser(id);
    }
}
