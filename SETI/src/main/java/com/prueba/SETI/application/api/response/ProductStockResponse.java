package com.prueba.SETI.application.api.response;

public record ProductStockResponse(
        String branchId,
        String branchName,
        String productId,
        String productName,
        int stock
) {
}
