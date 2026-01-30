package com.prueba.SETI.infrastructure.entryPoint.apiRest.response;

public record ProductStockResponse(
        String branchId,
        String branchName,
        String productId,
        String productName,
        int stock
) {
}
