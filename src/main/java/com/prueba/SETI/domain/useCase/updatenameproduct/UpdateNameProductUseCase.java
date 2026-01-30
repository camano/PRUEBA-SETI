package com.prueba.SETI.domain.useCase.updatenameproduct;


import com.prueba.SETI.domain.model.product.Product;
import com.prueba.SETI.domain.model.product.gateway.ProductRepositoryPort;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.naming.InvalidNameException;
@Service
@RequiredArgsConstructor
public class UpdateNameProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public Mono<Product> execute(String productId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new InvalidNameException("Product name cannot be empty"));
        }

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepositoryPort.updateName(productId, newName));
    }
}
