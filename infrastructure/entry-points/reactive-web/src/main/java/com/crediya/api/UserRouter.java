package com.crediya.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {

        @Bean
        public RouterFunction<ServerResponse> routes(UserHandler handler) {
            return RouterFunctions.route()
                    .POST("/users", handler::createUser)
                    .GET("/users", handler::getAllUsers)
                    .GET("/users/{dni}", handler::getUserByDni)
                    .PUT("/users", handler::updateUser)
                    .DELETE("/users/{id}", handler::deleteUser)
                    .build();
        }


}
