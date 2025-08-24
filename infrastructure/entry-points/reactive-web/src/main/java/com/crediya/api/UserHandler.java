package com.crediya.api;

import com.crediya.api.dto.CreateUserDTO;
import com.crediya.api.dto.EditUserDTO;
import com.crediya.api.dto.OutUserDTO;
import com.crediya.api.mapper.UserDTOMapper;
import com.crediya.model.user.User;
import com.crediya.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(CreateUserDTO.class)
                .map(userDTOMapper::toModel)           // DTO → dominio
                .flatMap(userUseCase::createUser)      // lógica de dominio
                .map(userDTOMapper::toResponse)        // dominio → DTO salida
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
    }


    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return userUseCase.listUsers()
                .collectList()
                .map(userDTOMapper::toResponseList) // lista de dominio → lista DTO
                .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list));
    }


    public Mono<ServerResponse> getUserByDni(ServerRequest request) {
        Long dni = Long.valueOf(request.pathVariable("dni"));
        return userUseCase.getUserByDni(dni)
                .map(userDTOMapper::toResponse)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> updateUser(ServerRequest request) {
        return request.bodyToMono(EditUserDTO.class)
                .map(userDTOMapper::toModel)
                .flatMap(userUseCase::updateUser)
                .map(userDTOMapper::toResponse)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto));
    }


    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return userUseCase.deleteUser(id)
                .then(ServerResponse.noContent().build());
    }

}
