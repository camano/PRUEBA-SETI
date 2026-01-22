package com.prueba.SETI.application.service;

import com.prueba.SETI.application.api.response.ProductStockResponse;
import com.prueba.SETI.domain.model.Branch;
import com.prueba.SETI.domain.model.Product;
import com.prueba.SETI.domain.ports.BranchRepositoryPort;
import com.prueba.SETI.domain.ports.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private StockService useCase;

    @Test
    void shouldReturnTopStockProductPerBranch() {
        // given
        String franchiseId = "f-1";

        Branch branch1 = new Branch("b-1", "Sucursal Norte");
        Branch branch2 = new Branch("b-2", "Sucursal Sur");

        Product product1 = new Product("p-1", "Producto A", 100);
        Product product2 = new Product("p-2", "Producto B", 50);

        when(branchRepositoryPort.findByFranchiseId(franchiseId))
                .thenReturn(Flux.just(branch1, branch2));

        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b-1"))
                .thenReturn(Mono.just(product1));

        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b-2"))
                .thenReturn(Mono.just(product2));

        // when
        Flux<ProductStockResponse> result = useCase.getTopStockByFranchise(franchiseId);

        // then
        StepVerifier.create(result)
                .expectNextMatches(r ->
                        r.branchId().equals("b-1") &&
                                r.productId().equals("p-1") &&
                                r.stock() == 100
                )
                .expectNextMatches(r ->
                        r.branchId().equals("b-2") &&
                                r.productId().equals("p-2") &&
                                r.stock() == 50
                )
                .verifyComplete();

        verify(branchRepositoryPort).findByFranchiseId(franchiseId);
        verify(productRepositoryPort).findTopByBranchIdOrderByStockDesc("b-1");
        verify(productRepositoryPort).findTopByBranchIdOrderByStockDesc("b-2");
    }

    @Test
    void shouldReturnEmptyWhenFranchiseHasNoBranches() {
        // given
        when(branchRepositoryPort.findByFranchiseId("f-empty"))
                .thenReturn(Flux.empty());

        // when
        Flux<ProductStockResponse> result =
                useCase.getTopStockByFranchise("f-empty");

        // then
        StepVerifier.create(result)
                .verifyComplete();

        verify(branchRepositoryPort).findByFranchiseId("f-empty");
        verifyNoInteractions(productRepositoryPort);
    }

    @Test
    void shouldSkipBranchWithoutProducts() {
        // given
        String franchiseId = "f-1";
        Branch branch = new Branch("b-1", "Sucursal sin productos");

        when(branchRepositoryPort.findByFranchiseId(franchiseId))
                .thenReturn(Flux.just(branch));

        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b-1"))
                .thenReturn(Mono.empty());

        // when
        Flux<ProductStockResponse> result =
                useCase.getTopStockByFranchise(franchiseId);

        // then
        StepVerifier.create(result)
                .verifyComplete();

        verify(branchRepositoryPort).findByFranchiseId(franchiseId);
        verify(productRepositoryPort).findTopByBranchIdOrderByStockDesc("b-1");
    }

}