package com.prueba.SETI.domain.useCase.updatenamefranchise;


import com.prueba.SETI.domain.model.Sequence.gateway.SequenceGeneratorPort;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;


@ExtendWith(MockitoExtension.class)
class UpdateNameFranchiseUseCaseTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    @Mock
    private SequenceGeneratorPort sequenceGeneratorPort;

    @InjectMocks
    private UpdateNameFranchiseUseCase useCase;

    @Test
    void shouldUpdateFranchiseNameSuccessfully() {
        // given
        String franchiseId = "f-1";
        String newName = "Nueva Franquicia";

        Franchise existing = new Franchise(franchiseId, "Viejo Nombre");
        Franchise updated = new Franchise(franchiseId, newName);

        when(franchiseRepositoryPort.findById(franchiseId))
                .thenReturn(Mono.just(existing));

        when(franchiseRepositoryPort.updateName(franchiseId, newName))
                .thenReturn(Mono.just(updated));

        // when
        Mono<Franchise> result = useCase.execute(franchiseId, newName);

        // then
        StepVerifier.create(result)
                .expectNextMatches(f ->
                        f.getId().equals(franchiseId)
                                && f.getName().equals(newName)
                )
                .verifyComplete();

        verify(franchiseRepositoryPort).findById(franchiseId);
        verify(franchiseRepositoryPort).updateName(franchiseId, newName);
    }
    @Test
    void shouldFailWhenFranchiseNotFound() {
        // given
        String franchiseId = "f-404";

        when(franchiseRepositoryPort.findById(franchiseId))
                .thenReturn(Mono.empty());

        // when
        Mono<Franchise> result = useCase.execute(franchiseId, "Nuevo");

        // then
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(franchiseRepositoryPort).findById(franchiseId);
        verify(franchiseRepositoryPort, never()).updateName(any(), any());
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        // when
        Mono<Franchise> result = useCase.execute("f-1", " ");

        // then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(franchiseRepositoryPort);
    }

}