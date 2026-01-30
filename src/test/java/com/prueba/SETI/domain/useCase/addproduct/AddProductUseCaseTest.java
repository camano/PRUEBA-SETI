package com.prueba.SETI.domain.useCase.addproduct;


import com.prueba.SETI.domain.model.Sequence.gateway.SequenceGeneratorPort;
import com.prueba.SETI.domain.model.branch.Branch;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductUseCaseTest {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private SequenceGeneratorPort sequenceGeneratorPort;

    @InjectMocks
    private AddProductUseCase useCase;


    @Test
    void shouldCreateProductSuccessfully() {
        // given
        String branchId = "b-1";
        String productId = "p-1";
        String name = "Coca Cola";
        int stock = 10;

        Branch branch = new Branch(branchId, "Sucursal Centro");
        Product savedProduct = new Product(productId, name, stock);

        when(branchRepositoryPort.findById(branchId))
                .thenReturn(Mono.just(branch));

        when(sequenceGeneratorPort.nextProductId())
                .thenReturn(Mono.just(productId));

        when(productRepositoryPort.save(eq(branchId), any(Product.class)))
                .thenReturn(Mono.just(savedProduct));

        // when
        Mono<Product> result = useCase.execute(branchId, name, stock);

        // then
        StepVerifier.create(result)
                .expectNextMatches(product ->
                        product.getId().equals(productId)
                                && product.getName().equals(name)
                                && product.getStock() == stock
                )
                .verifyComplete();

        verify(branchRepositoryPort).findById(branchId);
        verify(sequenceGeneratorPort).nextProductId();
        verify(productRepositoryPort).save(eq(branchId), any(Product.class));
    }

    @Test
    void shouldFailWhenBranchNotFound() {
        // given
        when(branchRepositoryPort.findById("b-404"))
                .thenReturn(Mono.empty());

        // when
        Mono<Product> result = useCase.execute("b-404", "Producto", 5);

        // then
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();

        verify(branchRepositoryPort).findById("b-404");
        verifyNoInteractions(sequenceGeneratorPort, productRepositoryPort);
    }

    @Test
    void shouldFailWhenSequenceFails() {
        // given
        when(branchRepositoryPort.findById("b-1"))
                .thenReturn(Mono.just(new Branch("b-1", "Sucursal")));

        when(sequenceGeneratorPort.nextProductId())
                .thenReturn(Mono.error(new RuntimeException("Sequence error")));

        // when
        Mono<Product> result = useCase.execute("b-1", "Producto", 5);

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(sequenceGeneratorPort).nextProductId();
        verifyNoInteractions(productRepositoryPort);
    }

    @Test
    void shouldFailWhenSaveFails() {
        // given
        when(branchRepositoryPort.findById("b-1"))
                .thenReturn(Mono.just(new Branch("b-1", "Sucursal")));

        when(sequenceGeneratorPort.nextProductId())
                .thenReturn(Mono.just("p-2"));

        when(productRepositoryPort.save(eq("b-1"), any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        // when
        Mono<Product> result = useCase.execute("b-1", "Producto", 5);

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(productRepositoryPort).save(eq("b-1"), any(Product.class));
    }

}