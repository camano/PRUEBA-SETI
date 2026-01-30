package co.com.bancolombia.usecase.generatefranchise;

import co.com.bancolombia.model.branch.gateways.SequenceGeneratorPort;
import co.com.bancolombia.model.franchise.CreateFranchiseRequest;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;


import reactor.core.publisher.Mono;



@RequiredArgsConstructor
public class GenerateFranchiseUseCase {


    private final FranchiseRepository franchiseRepositoryPort;
    private final SequenceGeneratorPort sequenceGeneratorPort;

    public Mono<Franchise> execute(CreateFranchiseRequest request) {

        return sequenceGeneratorPort.nextFranchiseId()
                .map(franchiseId -> new Franchise(franchiseId, request.name()))
                .flatMap(franchiseRepositoryPort::save);
    }


}
