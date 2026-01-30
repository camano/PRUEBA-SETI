package com.prueba.SETI.domain.model.branch.gateway;

import com.prueba.SETI.domain.model.branch.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepositoryPort {
    Mono<Branch> save(String franchiseId, Branch branch);
    Mono<Branch> findById(String id);
    Flux<Branch> findByFranchiseId(String franchiseId);
    Mono<Branch> updateName(String id, String newName);

}
