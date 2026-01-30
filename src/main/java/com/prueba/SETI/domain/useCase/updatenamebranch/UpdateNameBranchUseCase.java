package com.prueba.SETI.domain.useCase.updatenamebranch;


import com.prueba.SETI.domain.model.branch.Branch;
import com.prueba.SETI.domain.model.branch.gateway.BranchRepositoryPort;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class UpdateNameBranchUseCase {
    private final BranchRepositoryPort branchRepository;

    public Mono<Branch> execute(String branchId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("branch name cannot be empty"));
        }

        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new NotFoundException("branch not found")))
                .flatMap(branch -> branchRepository.updateName(branchId, newName));
    }
}
