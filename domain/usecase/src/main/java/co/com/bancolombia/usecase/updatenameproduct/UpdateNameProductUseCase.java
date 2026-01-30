package co.com.bancolombia.usecase.updatenameproduct;

import co.com.bancolombia.model.exception.NotFoundException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import javax.naming.InvalidNameException;

@RequiredArgsConstructor
public class UpdateNameProductUseCase {
    private final ProductRepository productRepositoryPort;

    public Mono<Product> execute(String productId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new InvalidNameException("Product name cannot be empty"));
        }

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepositoryPort.updateName(productId, newName));
    }
}
