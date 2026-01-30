package co.com.bancolombia.usecase.getstocktopfranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.ProductStockResponse;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetStockTopFranchiseUseCase {
    private final BranchRepository branchRepositoryPort;
    private final ProductRepository productRepositoryPort;
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
