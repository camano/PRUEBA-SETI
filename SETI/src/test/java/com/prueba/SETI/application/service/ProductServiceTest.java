package com.prueba.SETI.application.service;

import com.prueba.SETI.application.exception.NotFoundException;
import com.prueba.SETI.domain.model.Branch;
import com.prueba.SETI.domain.model.Product;
import com.prueba.SETI.domain.ports.BranchRepositoryPort;
import com.prueba.SETI.domain.ports.ProductRepositoryPort;
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
class ProductServiceTest {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private SequenceGeneratorPort sequenceGeneratorPort;

    @InjectMocks
    private ProductService useCase;

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
        Mono<Void> result = useCase.deleteProduct(productId);

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
        Mono<Void> result = useCase.deleteProduct("p-404");

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
        Mono<Void> result = useCase.deleteProduct("p-1");

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(productRepositoryPort).deleteById("p-1");


    }

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
        Mono<Product> result = useCase.updateProductName(productId, newName);

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
        Mono<Product> result = useCase.updateProductName("p-1", "   ");

        // then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(productRepositoryPort);
    }

    @Test
    void shouldFailWhenProductNotFound3() {
        // given
        when(productRepositoryPort.findById("p-404"))
                .thenReturn(Mono.empty());

        // when
        Mono<Product> result = useCase.updateProductName("p-404", "Nuevo");

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
        Mono<Product> result = useCase.updateProductName("p-1", "Nuevo");

        // then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(productRepositoryPort).updateName("p-1", "Nuevo");
    }
}