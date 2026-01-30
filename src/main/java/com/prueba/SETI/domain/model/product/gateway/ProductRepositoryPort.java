package com.prueba.SETI.domain.model.product.gateway;

import com.prueba.SETI.domain.model.product.Product;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {
    Mono<Product> save(String branchId, Product product);
    Mono<Product> findById(String productId);
    Mono<Product> updateStock(String productId, int newStock);
    Mono<Void> deleteById(String productId);
    Mono<Product>findTopByBranchIdOrderByStockDesc(String branchId);
    Mono<Product> updateName(String id, String newName);
}
