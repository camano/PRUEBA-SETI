package com.prueba.SETI.domain.useCase.updatestock;

import com.prueba.SETI.domain.model.branch.gateway.BranchRepositoryPort;
import com.prueba.SETI.domain.model.product.Product;
import com.prueba.SETI.domain.model.product.gateway.ProductRepositoryPort;
import com.prueba.SETI.domain.useCase.updatenameproduct.UpdateNameProductUseCase;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStockUseCaseTest {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;


    @InjectMocks
    private UpdateStockUseCase useCase;

    @Test
    void shouldUpdateStockSuccessfully() {
        // given
        String productId = "p-1";
        int newStock = 20;

        Product existing = new Product(productId, "Producto", 10);
        Product updated = new Product(productId, "Producto", newStock);

        when(productRepositoryPort.findById(productId))
                .thenReturn(Mono.just(existing));

        when(productRepositoryPort.updateStock(productId, newStock))
                .thenReturn(Mono.just(updated));

        // when
        Mono<Product> result = useCase.updateStock(productId, newStock);

        // then
        StepVerifier.create(result)
                .expectNextMatches(p ->
                        p.getId().equals(productId)
                                && p.getStock() == newStock
                )
                .verifyComplete();

        verify(productRepositoryPort).findById(productId);
        verify(productRepositoryPort).updateStock(productId, newStock);
    }

    @Test
    void shouldFailWhenProductNotFound() {
        // given
        when(productRepositoryPort.findById("p-404"))
                .thenReturn(Mono.empty());

        // when
        Mono<Product> result = useCase.updateStock("p-404", 10);

        // then
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(productRepositoryPort).findById("p-404");
        verify(productRepositoryPort, never()).updateStock(any(), anyInt());
    }

    @Test
    void shouldFailWhenStockIsNegative() {
        // when
        Mono<Product> result = useCase.updateStock("p-1", -5);

        // then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(productRepositoryPort);
    }

    @Test
    void shouldFailWhenUpdateFails() {
        // given
        when(productRepositoryPort.findById("p-1"))
                .thenReturn(Mono.just(new Product("p-1", "Producto", 5)));

        when(productRepositoryPort.updateStock("p-1", 15))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        // when
        Mono<Product> result = useCase.updateStock("p-1", 15);

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(productRepositoryPort).updateStock("p-1", 15);
    }
}