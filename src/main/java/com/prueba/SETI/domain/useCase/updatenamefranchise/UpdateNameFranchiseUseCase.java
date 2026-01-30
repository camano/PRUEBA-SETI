package com.prueba.SETI.domain.useCase.updatenamefranchise;


import com.prueba.SETI.domain.model.franchise.Franchise;
import com.prueba.SETI.domain.model.franchise.gateway.FranchiseRepositoryPort;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class UpdateNameFranchiseUseCase {
    private final FranchiseRepositoryPort franchiseRepositoryPort;

    public Mono<Franchise> execute(String franchiseId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("Franchise name cannot be empty"));
        }

        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .flatMap(franchise -> franchiseRepositoryPort.updateName(franchiseId, newName));

    }
}
