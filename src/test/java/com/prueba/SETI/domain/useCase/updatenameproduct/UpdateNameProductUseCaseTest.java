package com.prueba.SETI.domain.useCase.updatenameproduct;


import com.prueba.SETI.domain.model.branch.gateway.BranchRepositoryPort;
import com.prueba.SETI.domain.model.product.Product;
import com.prueba.SETI.domain.model.product.gateway.ProductRepositoryPort;

import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.naming.InvalidNameException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateNameProductUseCaseTest {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;


    @InjectMocks
    private UpdateNameProductUseCase useCase;


    @Test
    void shouldUpdateProductNameSuccessfully() {
        // given
        String productId = "p-1";
        String newName = "Producto actualizado";

        Product existing = new Product(productId, "Viejo", 10);
        Product updated = new Product(productId, newName, 10);

        when(productRepositoryPort.findById(productId))
                .thenReturn(Mono.just(existing));

        when(productRepositoryPort.updateName(productId, newName))
                .thenReturn(Mono.just(updated));

        // when
        Mono<Product> result = useCase.execute(productId, newName);

        // then
        StepVerifier.create(result)
                .expectNextMatches(p ->
                        p.getId().equals(productId) &&
                                p.getName().equals(newName)
                )
                .verifyComplete();

        verify(productRepositoryPort).findById(productId);
        verify(productRepositoryPort).updateName(productId, newName);
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        // when
        Mono<Product> result = useCase.execute("p-1", "   ");

        // then
        StepVerifier.create(result)
                .expectError(InvalidNameException.class)
                .verify();

        verifyNoInteractions(productRepositoryPort);
    }

    @Test
    void shouldFailWhenProductNotFound3() {
        // given
        when(productRepositoryPort.findById("p-404"))
                .thenReturn(Mono.empty());

        // when
        Mono<Product> result = useCase.execute("p-404", "Nuevo");

        // then
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(productRepositoryPort).findById("p-404");
        verify(productRepositoryPort, never()).updateName(any(), any());
    }

    @Test
    void shouldFailWhenUpdateFails2() {
        // given
        when(productRepositoryPort.findById("p-1"))
                .thenReturn(Mono.just(new Product("p-1", "Viejo", 5)));

        when(productRepositoryPort.updateName("p-1", "Nuevo"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        // when
        Mono<Product> result = useCase.execute("p-1", "Nuevo");

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(productRepositoryPort).updateName("p-1", "Nuevo");
    }

}