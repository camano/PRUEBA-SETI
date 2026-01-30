package com.prueba.SETI.domain.useCase.getstocktopfranchise;


import com.prueba.SETI.domain.model.branch.Branch;
import com.prueba.SETI.domain.model.branch.gateway.BranchRepositoryPort;
import com.prueba.SETI.domain.model.product.Product;
import com.prueba.SETI.domain.model.product.gateway.ProductRepositoryPort;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.response.ProductStockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class GetStockTopFranchiseUseCase {
    private final BranchRepositoryPort branchRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    public Flux<ProductStockResponse> execute(String franchiseId) {

        return branchRepositoryPort.findByFranchiseId(franchiseId)
                .flatMap(branch ->
                        Mono.zip(
                                Mono.just(branch),
                                productRepositoryPort
                                        .findTopByBranchIdOrderByStockDesc(branch.getId())
                                        .switchIfEmpty(Mono.empty())
                        )
                )
                .filter(tuple -> tuple.getT2() != null)
                .map(tuple -> {
                    Branch branch = tuple.getT1();
                    Product product = tuple.getT2();
                    return new ProductStockResponse(
                            branch.getId(),
                            branch.getName(),
                            product.getId(),
                            product.getName(),
                            product.getStock()
                    );
                });
    }
}
