package co.com.bancolombia.usecase.addproduct;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.branch.gateways.SequenceGeneratorPort;
import co.com.bancolombia.model.exception.NotFoundException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddProductUseCase {
    private final BranchRepository branchRepositoryPort;
    private final ProductRepository productRepositoryPort;
    private final SequenceGeneratorPort sequenceGeneratorPort;

    public Mono<Product> execute(String branchId, String name, int stock) {

        return branchRepositoryPort.findById(branchId)
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(branch ->
                        sequenceGeneratorPort.nextProductId()
                                .map(productId -> new Product(productId, name, stock))
                                .flatMap(product -> productRepositoryPort.save(branchId, product))
                );
    }
}
