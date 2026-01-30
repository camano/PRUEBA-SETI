package com.prueba.SETI.infrastructure.adapter.persistence.repository;

import com.prueba.SETI.infrastructure.adapter.persistence.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BranchR2dbcRepository extends ReactiveCrudRepository<BranchEntity, String> {

    Flux<BranchEntity> findByFranchiseId(String franchiseId);
}
