package co.com.bancolombia.usecase.addbranch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.branch.gateways.SequenceGeneratorPort;
import co.com.bancolombia.model.exception.NotFoundException;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddBranchUseCase {
    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final SequenceGeneratorPort sequenceGeneratorPort;

    public Mono<Branch> execute(String franchiseId, String branchName) {

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .flatMap(franchise ->
                        sequenceGeneratorPort.nextBranchId()
                                .map(branchId -> new Branch(branchId, branchName))
                                .flatMap(branch -> branchRepository.save(franchiseId, branch))
                );
    }
}
