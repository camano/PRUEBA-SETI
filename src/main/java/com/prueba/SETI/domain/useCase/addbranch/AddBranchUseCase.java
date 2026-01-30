package com.prueba.SETI.domain.useCase.addbranch;


import com.prueba.SETI.domain.model.Sequence.gateway.SequenceGeneratorPort;
import com.prueba.SETI.domain.model.branch.Branch;
import com.prueba.SETI.domain.model.branch.gateway.BranchRepositoryPort;
import com.prueba.SETI.domain.model.franchise.gateway.FranchiseRepositoryPort;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AddBranchUseCase {
    private final FranchiseRepositoryPort franchiseRepository;
    private final BranchRepositoryPort branchRepository;
    private final SequenceGeneratorPort sequenceGenerator;

    public Mono<Branch> execute(String franchiseId, String branchName) {

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .flatMap(franchise ->
                        sequenceGenerator.nextBranchId()
                                .map(branchId -> new Branch(branchId, branchName))
                                .flatMap(branch -> branchRepository.save(franchiseId, branch))
                );
    }
}
