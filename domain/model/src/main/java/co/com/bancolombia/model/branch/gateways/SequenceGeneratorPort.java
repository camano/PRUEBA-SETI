package co.com.bancolombia.model.branch.gateways;

import reactor.core.publisher.Mono;

public interface SequenceGeneratorPort {
    Mono<String> nextFranchiseId();
    Mono<String> nextBranchId();

    Mono<String> nextProductId();
}
