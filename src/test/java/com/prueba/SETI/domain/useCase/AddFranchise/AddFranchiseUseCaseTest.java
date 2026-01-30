package com.prueba.SETI.domain.useCase.AddFranchise;

import com.prueba.SETI.domain.model.Sequence.gateway.SequenceGeneratorPort;
import com.prueba.SETI.domain.model.franchise.Franchise;
import com.prueba.SETI.domain.model.franchise.gateway.FranchiseRepositoryPort;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.request.CreateFranchiseRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class AddFranchiseUseCaseTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    @Mock
    private SequenceGeneratorPort sequenceGeneratorPort;

    @InjectMocks
    private AddFranchiseUseCase useCase;


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

}