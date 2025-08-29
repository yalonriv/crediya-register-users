package com.crediya.api;

import com.crediya.api.dto.EditUserDTO;
import com.crediya.api.dto.OutUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Usuarios", description = "Operaciones sobre usuarios")
public class UserRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/users",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "createUser",
                    operation = @Operation(
                            operationId = "createUser",
                            summary = "Crear un usuario",
                            tags = {"User"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario creado correctamente",
                                            content = @Content(schema = @Schema(implementation = OutUserDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/users",
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "getAllUsers",
                    operation = @Operation(
                            summary = "Obtener todos los usuarios",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Lista de usuarios"
                            )
                    )
            ),
            @RouterOperation(
                    path = "/users/{dni}",
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "getUserByDni",
                    operation = @Operation(
                            summary = "Obtener usuario por DNI",
                            parameters = @Parameter(
                                    name = "dni",
                                    description = "Número de documento",
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = OutUserDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Usuario no encontrado"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/users",
                    method = RequestMethod.PUT,
                    beanClass = UserHandler.class,
                    beanMethod = "updateUser",
                    operation = @Operation(
                            summary = "Actualizar usuario",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de usuario a actualizar",
                                    content = @Content(
                                            schema = @Schema(implementation = EditUserDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario actualizado",
                                            content = @Content(schema = @Schema(implementation = OutUserDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Usuario no encontrado"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/users/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = UserHandler.class,
                    beanMethod = "deleteUser",
                    operation = @Operation(
                            summary = "Eliminar usuario por ID",
                            parameters = @Parameter(
                                    name = "id",
                                    description = "Identificador del usuario",
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Usuario eliminado"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Usuario no encontrado"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routes(UserHandler handler) {
        return route()
                .POST("/users", handler::createUser)
                .GET("/users", handler::getAllUsers)
                .GET("/users/{dni}", handler::getUserByDni)
                .PUT("/users", handler::updateUser)
                .DELETE("/users/{id}", handler::deleteUser)
                .build();
    }
}