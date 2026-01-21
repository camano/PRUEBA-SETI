package com.prueba.SETI.domain.ports;

import reactor.core.publisher.Mono;

public interface SequenceGeneratorPort {
    Mono<String> nextFranchiseId();
    Mono<String> nextBranchId();

    Mono<String> nextProductId();
}
