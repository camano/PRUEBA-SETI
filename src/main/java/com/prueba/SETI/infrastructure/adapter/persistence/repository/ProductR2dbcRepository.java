package com.prueba.SETI.infrastructure.adapter.persistence.repository;

import com.prueba.SETI.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductR2dbcRepository
        extends ReactiveCrudRepository<ProductEntity, String> {

    Mono<ProductEntity> findFirstByBranchIdOrderByStockDesc(String branchId);
}
