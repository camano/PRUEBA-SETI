package co.com.bancolombia.usecase.deleteproduct;

import co.com.bancolombia.model.exception.NotFoundException;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final ProductRepository productRepositoryPort;

    public Mono<Void> execute(String productId) {

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepositoryPort.deleteById(productId));
    }
}
