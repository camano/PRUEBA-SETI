package com.prueba.SETI.application.service;

import com.prueba.SETI.application.exception.NotFoundException;
import com.prueba.SETI.domain.model.Branch;
import com.prueba.SETI.domain.model.Franchise;
import com.prueba.SETI.domain.ports.BranchRepositoryPort;
import com.prueba.SETI.domain.ports.FranchiseRepositoryPort;
import com.prueba.SETI.domain.ports.SequenceGeneratorPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {
    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private BranchRepositoryPort branchRepository;

    @Mock
    private SequenceGeneratorPort sequenceGeneratorPort;

    @InjectMocks
    private BranchService useCase;

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

    @Test
    void shouldUpdateBranchNameSuccessfully() {
        // given
        String branchId = "b-1";
        String newName = "Sucursal Norte";

        Branch existing = new Branch(branchId, "Sucursal Vieja");
        Branch updated = new Branch(branchId, newName);

        when(branchRepository.findById(branchId))
                .thenReturn(Mono.just(existing));

        when(branchRepository.updateName(branchId, newName))
                .thenReturn(Mono.just(updated));

        // when
        Mono<Branch> result = useCase.updateBranchName(branchId, newName);

        // then
        StepVerifier.create(result)
                .expectNextMatches(b ->
                        b.getId().equals(branchId)
                                && b.getName().equals(newName)
                )
                .verifyComplete();

        verify(branchRepository).findById(branchId);
        verify(branchRepository).updateName(branchId, newName);
    }

    @Test
    void shouldFailWhenBranchNotFound() {
        // given
        when(branchRepository.findById("b-404"))
                .thenReturn(Mono.empty());

        // when
        Mono<Branch> result = useCase.updateBranchName("b-404", "Nuevo");

        // then
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(branchRepository).findById("b-404");
        verify(branchRepository, never()).updateName(any(), any());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        // when
        Mono<Branch> result = useCase.updateBranchName("b-1", " ");

        // then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(branchRepository);
    }
}