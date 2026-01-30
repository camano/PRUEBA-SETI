package co.com.bancolombia.model.product;

public record ProductStockResponse(
        String branchId,
        String branchName,
        String productId,
        String productName,
        int stock
) {
}
