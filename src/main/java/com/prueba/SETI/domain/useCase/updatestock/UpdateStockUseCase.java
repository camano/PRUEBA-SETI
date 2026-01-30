package com.prueba.SETI.domain.useCase.updatestock;


import com.prueba.SETI.domain.model.product.Product;
import com.prueba.SETI.domain.model.product.gateway.ProductRepositoryPort;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class UpdateStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public Mono<Product> updateStock(String productId, int newStock) {

        if (newStock < 0) {
            return Mono.error(new IllegalArgumentException("Stock cannot be negative"));
        }

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepositoryPort.updateStock(productId, newStock));
    }
}
