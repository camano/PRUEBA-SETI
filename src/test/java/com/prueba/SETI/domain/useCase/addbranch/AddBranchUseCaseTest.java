package com.prueba.SETI.domain.useCase.addbranch;

import com.prueba.SETI.domain.model.Sequence.gateway.SequenceGeneratorPort;
import com.prueba.SETI.domain.model.branch.Branch;
import com.prueba.SETI.domain.model.branch.gateway.BranchRepositoryPort;
import com.prueba.SETI.domain.model.franchise.Franchise;
import com.prueba.SETI.domain.model.franchise.gateway.FranchiseRepositoryPort;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class AddBranchUseCaseTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private BranchRepositoryPort branchRepository;

    @Mock
    private SequenceGeneratorPort sequenceGeneratorPort;

    @InjectMocks
    private AddBranchUseCase useCase;

    @Test
    void shouldCreateBranchSuccessfully() {
        // given
        String franchiseId = "f-1";
        String branchName = "Sucursal Centro";
        String branchId = "b-1";

        Franchise franchise = new Franchise(franchiseId, "Franquicia Test");
        Branch savedBranch = new Branch(branchId, branchName);

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(franchise));

        when(sequenceGeneratorPort.nextBranchId())
                .thenReturn(Mono.just(branchId));

        when(branchRepository.save(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        // when
        Mono<Branch> result = useCase.execute(franchiseId, branchName);

        // then
        StepVerifier.create(result)
                .expectNextMatches(branch ->
                        branch.getId().equals(branchId)
                                && branch.getName().equals(branchName)
                )
                .verifyComplete();

        verify(franchiseRepository).findById(franchiseId);
        verify(sequenceGeneratorPort).nextBranchId();
        verify(branchRepository).save(eq(franchiseId), any(Branch.class));
    }

    @Test
    void shouldFailWhenFranchiseNotFound() {
        // given
        String franchiseId = "f-404";

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.empty());

        // when
        Mono<Branch> result = useCase.execute(franchiseId, "Sucursal");

        // then
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(franchiseRepository).findById(franchiseId);
        verifyNoInteractions(sequenceGeneratorPort, branchRepository);
    }

    @Test
    void shouldFailWhenSequenceFails() {
        // given
        String franchiseId = "f-1";

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(new Franchise(franchiseId, "Test")));

        when(sequenceGeneratorPort.nextBranchId())
                .thenReturn(Mono.error(new RuntimeException("Sequence error")));

        // when
        Mono<Branch> result = useCase.execute(franchiseId, "Sucursal");

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(sequenceGeneratorPort).nextBranchId();
        verifyNoInteractions(branchRepository);
    }

    @Test
    void shouldFailWhenSaveFails() {
        // given
        String franchiseId = "f-1";
        String branchId = "b-2";

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(new Franchise(franchiseId, "Test")));

        when(sequenceGeneratorPort.nextBranchId())
                .thenReturn(Mono.just(branchId));

        when(branchRepository.save(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        // when
        Mono<Branch> result = useCase.execute(franchiseId, "Sucursal");

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(branchRepository).save(eq(franchiseId), any(Branch.class));
    }

}