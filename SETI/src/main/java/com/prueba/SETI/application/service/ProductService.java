package com.prueba.SETI.application.service;

import com.prueba.SETI.application.exception.NotFoundException;
import com.prueba.SETI.domain.model.Product;
import com.prueba.SETI.domain.ports.BranchRepositoryPort;
import com.prueba.SETI.domain.ports.ProductRepositoryPort;
import com.prueba.SETI.domain.ports.SequenceGeneratorPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final BranchRepositoryPort branchRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final SequenceGeneratorPort sequenceGeneratorPort;

    public Mono<Product> execute(String branchId, String name, int stock) {


        return branchRepositoryPort.findById(branchId)
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .doOnSubscribe(s ->
                        log.info("Agregando producto [{}] a sucursal [{}]", name, branchId)
                )
                .flatMap(branch ->
                        sequenceGeneratorPort.nextProductId()
                                .map(productId -> new Product(productId, name, stock))
                                .flatMap(product -> productRepositoryPort.save(branchId, product))
                )
                .doOnNext(product -> log.info("Producto creado con id [{}]", product.getId()))
                .doOnError(error -> log.error("Error creando producto", error));
    }

    public Mono<Product> updateStock(String productId, int newStock) {
        if (newStock < 0) {
            return Mono.error(new IllegalArgumentException("Stock cannot be negative"));
        }
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepositoryPort.updateStock(productId, newStock))
                .doOnSubscribe(s -> log.info("Actualizando stock del producto [{}]", productId))
                .doOnNext(p -> log.info("Nuevo stock [{}] para producto [{}]", p.getStock(), productId))
                .doOnError(e -> log.error("Error actualizando stock", e));
    }

    public Mono<Void> deleteProduct(String productId) {

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepositoryPort.deleteById(productId))
                .doOnSubscribe(s -> log.info("Eliminando producto [{}]", productId))
                .doOnSuccess(v -> log.info("Producto eliminado correctamente [{}]", productId))
                .doOnError(e -> log.error("Error eliminando producto", e));
    }

    public Mono<Product> updateProductName(String productId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("Product name cannot be empty"));
        }

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(franchise -> productRepositoryPort.updateName(productId, newName))
                .doOnSubscribe(s -> log.info("Actualizando nombre de producto [{}]", productId))
                .doOnNext(f -> log.info("Nuevo nombre [{}] para producto [{}]", f.getName(), productId))
                .doOnError(e -> log.error("Error actualizando producto", e));
    }
}
