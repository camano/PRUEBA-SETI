package com.prueba.SETI.application.api.controller;

import com.prueba.SETI.application.api.request.CreateProductRequest;
import com.prueba.SETI.application.api.request.UpdateNameRequest;
import com.prueba.SETI.application.api.request.UpdateStockRequest;
import com.prueba.SETI.application.api.response.ProductStockResponse;
import com.prueba.SETI.application.service.ProductService;
import com.prueba.SETI.application.service.StockService;
import com.prueba.SETI.domain.model.Branch;
import com.prueba.SETI.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final StockService stockQueryService;

    @PostMapping("/add/{branchId}/products")
    public Mono<Product> addProduct(@PathVariable String branchId, @RequestBody CreateProductRequest request) {
        return productService.execute(branchId, request.name(), request.stock());
    }

    @PatchMapping("/{productId}/stock")
    public Mono<Product> updateStock(@PathVariable String productId, @RequestBody UpdateStockRequest request) {
        return productService.updateStock(productId, request.stock());
    }

    @DeleteMapping("/{productId}")
    public Mono<Void> deleteProduct(@PathVariable String productId) {
        return productService.deleteProduct(productId);
    }

    @GetMapping("/{franchiseId}/top-stock")
    public Flux<ProductStockResponse> getTopStock(
            @PathVariable String franchiseId
    ) {
        return stockQueryService.getTopStockByFranchise(franchiseId);
    }

    @PatchMapping("/{productId}/name")
    public Mono<Product> updateName(@PathVariable String productId, @RequestBody UpdateNameRequest request) {
        return productService.updateProductName(productId, request.name());
    }
}
