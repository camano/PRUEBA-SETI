package com.prueba.SETI.domain.useCase.deleteproduct;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;


    @InjectMocks
    private DeleteProductUseCase useCase;

    @Test
    void shouldDeleteProductSuccessfully() {
        // given
        String productId = "p-1";
        Product product = new Product(productId, "Producto", 10);

        when(productRepositoryPort.findById(productId))
                .thenReturn(Mono.just(product));

        when(productRepositoryPort.deleteById(productId))
                .thenReturn(Mono.empty());

        // when
        Mono<Void> result = useCase.execute(productId);

        // then
        StepVerifier.create(result)
                .verifyComplete();

        verify(productRepositoryPort).findById(productId);
        verify(productRepositoryPort).deleteById(productId);
    }

    @Test
    void shouldFailWhenProductNotFound2() {
        // given
        when(productRepositoryPort.findById("p-404"))
                .thenReturn(Mono.empty());

        // when
        Mono<Void> result = useCase.execute("p-404");

        // then
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(productRepositoryPort).findById("p-404");
        verify(productRepositoryPort, never()).deleteById(any());
    }

    @Test
    void shouldFailWhenDeleteFails() {
        // given
        when(productRepositoryPort.findById("p-1"))
                .thenReturn(Mono.just(new Product("p-1", "Producto", 5)));

        when(productRepositoryPort.deleteById("p-1"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        // when
        Mono<Void> result = useCase.execute("p-1");

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(productRepositoryPort).deleteById("p-1");


    }

}