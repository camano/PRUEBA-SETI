package co.com.bancolombia.usecase.updatestock;

import co.com.bancolombia.model.exception.NotFoundException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateStockUseCase {

    private final ProductRepository productRepositoryPort;

    public Mono<Product> updateStock(String productId, int newStock) {

        if (newStock < 0) {
            return Mono.error(new IllegalArgumentException("Stock cannot be negative"));
        }

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepositoryPort.updateStock(productId, newStock));
    }
}
