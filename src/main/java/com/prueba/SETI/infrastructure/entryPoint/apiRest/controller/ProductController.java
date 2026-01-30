package com.prueba.SETI.infrastructure.entryPoint.apiRest.controller;


import com.prueba.SETI.domain.model.product.Product;
import com.prueba.SETI.domain.useCase.addproduct.AddProductUseCase;
import com.prueba.SETI.domain.useCase.deleteproduct.DeleteProductUseCase;
import com.prueba.SETI.domain.useCase.getstocktopfranchise.GetStockTopFranchiseUseCase;
import com.prueba.SETI.domain.useCase.updatenameproduct.UpdateNameProductUseCase;
import com.prueba.SETI.domain.useCase.updatestock.UpdateStockUseCase;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.request.CreateProductRequest;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.request.UpdateNameRequest;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.request.UpdateStockRequest;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.response.ProductStockResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final AddProductUseCase productUseCaseAddProduct;
    private final UpdateStockUseCase productUseCaseUpdateStock;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateNameProductUseCase updateNameProductUseCase;
    private final GetStockTopFranchiseUseCase getStockTopFranchiseUseCase;


    @PostMapping("/add/{branchId}/products")
    public Mono<Product> addProduct(@PathVariable String branchId, @RequestBody CreateProductRequest request) {
        log.info("Agregando producto [{}] a sucursal [{}]", request.name(), branchId);

        return productUseCaseAddProduct.execute(branchId, request.name(), request.stock())
                .doOnSuccess(p -> log.info("Producto creado con id [{}]", p.getId()))
                .doOnError(e -> log.error("Error creando producto", e));
    }

    @PatchMapping("/{productId}/stock")
    public Mono<Product> updateStock(@PathVariable String productId, @RequestBody UpdateStockRequest request) {
        log.info("Actualizando stock del producto [{}]", productId);

        return productUseCaseUpdateStock.updateStock(productId, request.stock())
                .doOnSuccess(p -> log.info("Nuevo stock [{}] para producto [{}]", p.getStock(), productId))
                .doOnError(e -> log.error("Error actualizando stock", e));
    }

    @DeleteMapping("/{productId}")
    public Mono<Void> deleteProduct(@PathVariable String productId) {
        log.info("Eliminando producto [{}]", productId);

        return deleteProductUseCase.execute(productId)
                .doOnSuccess(v -> log.info("Producto eliminado correctamente [{}]", productId))
                .doOnError(e -> log.error("Error eliminando producto", e));
    }

    @GetMapping("/{franchiseId}/top-stock")
    public Flux<ProductStockResponse> getTopStock(
            @PathVariable String franchiseId
    ) {
        log.info("Consultando producto con mayor stock para franquicia [{}]", franchiseId);

        return getStockTopFranchiseUseCase.execute(franchiseId)
                .doOnComplete(() -> log.info("Consulta finalizada"))
                .doOnError(e -> log.error("Error en consulta top stock", e));
    }

    @PatchMapping("/{productId}/name")
    public Mono<Product> updateName(@PathVariable String productId, @RequestBody UpdateNameRequest request) {
        log.info("Actualizando nombre de producto [{}]", productId);

        return updateNameProductUseCase.execute(productId, request.name())
                .doOnSuccess(p -> log.info("Nuevo nombre [{}] para producto [{}]", p.getName(), productId))
                .doOnError(e -> log.error("Error actualizando producto", e));
    }
}
