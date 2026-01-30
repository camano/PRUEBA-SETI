package co.com.bancolombia.usecase.updatenamefranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.exception.NotFoundException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateNameFranchiseUseCase {
    private final FranchiseRepository franchiseRepositoryPort;

    public Mono<Franchise> execute(String franchiseId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("Franchise name cannot be empty"));
        }

        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .flatMap(franchise -> franchiseRepositoryPort.updateName(franchiseId, newName));

    }
}
