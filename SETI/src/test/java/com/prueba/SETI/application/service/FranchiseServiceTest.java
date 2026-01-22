package com.prueba.SETI.application.service;

import com.prueba.SETI.application.api.request.CreateFranchiseRequest;
import com.prueba.SETI.application.exception.NotFoundException;
import com.prueba.SETI.domain.model.Franchise;
import com.prueba.SETI.domain.ports.FranchiseRepositoryPort;
import com.prueba.SETI.domain.ports.SequenceGeneratorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    @Mock
    private SequenceGeneratorPort sequenceGeneratorPort;

    @InjectMocks
    private FranchiseService useCase;



    @Test
    void shouldCreateFranchiseSuccessfully() {

        // GIVEN
        CreateFranchiseRequest request =
                new CreateFranchiseRequest("KFC");

        when(sequenceGeneratorPort.nextFranchiseId())
                .thenReturn(Mono.just("f-1"));

        when(franchiseRepositoryPort.save(any(Franchise.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        // WHEN
        Mono<Franchise> result = useCase.execute(request);

        // THEN
        StepVerifier.create(result)
                .expectNextMatches(franchise ->
                        franchise.getId().equals("f-1") &&
                                franchise.getName().equals("KFC")
                )
                .verifyComplete();
    }


    @Test
    void shouldFailWhenRepositoryFails() {

        CreateFranchiseRequest request =
                new CreateFranchiseRequest("KFC");

        when(sequenceGeneratorPort.nextFranchiseId())
                .thenReturn(Mono.just("f-1"));

        when(franchiseRepositoryPort.save(any()))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.execute(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldFailWhenSequenceGeneratorFails() {

        CreateFranchiseRequest request =
                new CreateFranchiseRequest("KFC");

        when(sequenceGeneratorPort.nextFranchiseId())
                .thenReturn(Mono.error(new RuntimeException("Sequence error")));

        StepVerifier.create(useCase.execute(request))
                .expectError(RuntimeException.class)
                .verify();
    }

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
        Mono<Franchise> result = useCase.updateFranchiseName(franchiseId, newName);

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
        Mono<Franchise> result = useCase.updateFranchiseName(franchiseId, "Nuevo");

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
        Mono<Franchise> result = useCase.updateFranchiseName("f-1", " ");

        // then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(franchiseRepositoryPort);
    }
}