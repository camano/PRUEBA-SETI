package com.prueba.SETI.domain.useCase.deleteproduct;


import com.prueba.SETI.domain.model.product.gateway.ProductRepositoryPort;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public Mono<Void> execute(String productId) {

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepositoryPort.deleteById(productId));
    }
}
