package co.com.bancolombia.api.response;

public record ProductStockResponse(
        String branchId,
        String branchName,
        String productId,
        String productName,
        int stock
) {
}
