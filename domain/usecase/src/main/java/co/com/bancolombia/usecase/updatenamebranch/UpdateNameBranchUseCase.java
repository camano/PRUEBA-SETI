package co.com.bancolombia.usecase.updatenamebranch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateNameBranchUseCase {
    private final BranchRepository branchRepository;

    public Mono<Branch> execute(String branchId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("branch name cannot be empty"));
        }

        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new NotFoundException("branch not found")))
                .flatMap(branch -> branchRepository.updateName(branchId, newName));
    }
}
