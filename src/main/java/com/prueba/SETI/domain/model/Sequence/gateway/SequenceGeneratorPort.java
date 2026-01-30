package com.prueba.SETI.domain.model.Sequence.gateway;

import reactor.core.publisher.Mono;

public interface SequenceGeneratorPort {
    Mono<String> nextFranchiseId();
    Mono<String> nextBranchId();

    Mono<String> nextProductId();
}
