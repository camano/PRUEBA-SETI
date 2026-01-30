package com.prueba.SETI.domain.useCase.updatenamebranch;

import com.prueba.SETI.domain.model.branch.Branch;
import com.prueba.SETI.domain.model.branch.gateway.BranchRepositoryPort;
import com.prueba.SETI.domain.model.franchise.gateway.FranchiseRepositoryPort;
import com.prueba.SETI.domain.useCase.addbranch.AddBranchUseCase;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UpdateNameBranchUseCaseTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @Mock
    private BranchRepositoryPort branchRepository;

    @InjectMocks
    private UpdateNameBranchUseCase useCase;

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
        Mono<Branch> result = useCase.execute(branchId, newName);

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
        Mono<Branch> result = useCase.execute("b-404", "Nuevo");

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
        Mono<Branch> result = useCase.execute("b-1", " ");

        // then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(branchRepository);
    }

}