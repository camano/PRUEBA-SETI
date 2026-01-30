package com.prueba.SETI.domain.useCase.AddFranchise;


import com.prueba.SETI.domain.model.Sequence.gateway.SequenceGeneratorPort;
import com.prueba.SETI.domain.model.franchise.Franchise;
import com.prueba.SETI.domain.model.franchise.gateway.FranchiseRepositoryPort;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.request.CreateFranchiseRequest;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class AddFranchiseUseCase {


    private final FranchiseRepositoryPort franchiseRepositoryPort;
    private final SequenceGeneratorPort sequenceGeneratorPort;

    public Mono<Franchise> execute(CreateFranchiseRequest request) {

        return sequenceGeneratorPort.nextFranchiseId()
                .map(franchiseId -> new Franchise(franchiseId, request.name()))
                .flatMap(franchiseRepositoryPort::save);
    }


}
