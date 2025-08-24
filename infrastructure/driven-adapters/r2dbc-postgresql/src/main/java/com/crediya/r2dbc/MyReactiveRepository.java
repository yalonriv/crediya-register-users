package com.crediya.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import com.crediya.r2dbc.entities.UserEntity;

@Repository
public interface MyReactiveRepository extends
        ReactiveCrudRepository<UserEntity, Long>,
        org.springframework.data.repository.query.ReactiveQueryByExampleExecutor<UserEntity> {
    Mono<UserEntity> findByDniNumber(Long dniNumber);
    Mono<UserEntity> getUserByEmail(String email);

}

