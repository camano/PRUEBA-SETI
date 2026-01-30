package com.prueba.SETI.domain.model.franchise.gateway;

import com.prueba.SETI.domain.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepositoryPort {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Mono<Franchise> updateName(String id, String newName);
}
