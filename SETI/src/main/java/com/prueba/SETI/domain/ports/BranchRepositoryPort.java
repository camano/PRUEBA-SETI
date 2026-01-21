package com.prueba.SETI.domain.ports;

import com.prueba.SETI.domain.model.Branch;
import com.prueba.SETI.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepositoryPort {
    Mono<Branch> save(String franchiseId, Branch branch);
    Mono<Branch> findById(String id);
    Flux<Branch> findByFranchiseId(String franchiseId);
    Mono<Branch> updateName(String id, String newName);

}
