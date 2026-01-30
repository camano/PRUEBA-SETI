package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;

import co.com.bancolombia.r2dbc.repository.ProductR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {


    private final ProductR2dbcRepository repository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Product> save(String branchId, Product product) {

        return databaseClient.sql("""
            INSERT INTO products (id, name, stock, branch_id)
            VALUES (:id, :name, :stock, :branchId)
        """)
                .bind("id", product.getId())
                .bind("name", product.getName())
                .bind("stock", product.getStock())
                .bind("branchId", branchId)
                .fetch()
                .rowsUpdated()
                .map(rows ->
                        new Product(product.getId(), product.getName(), product.getStock())
                );
    }



    @Override
    public Mono<Product> findById(String productId) {
        return repository.findById(productId)
                .map(entity ->
                        new Product(
                                entity.getId().toString(),
                                entity.getName(),
                                entity.getStock()
                        )
                );
    }

    @Override
    public Mono<Product> updateStock(String productId, int newStock) {
        return repository.findById(productId)
                .switchIfEmpty(Mono.empty())
                .flatMap(entity -> {
                    entity.setStock(newStock);
                    return repository.save(entity);
                })
                .map(updated ->
                        new Product(
                                updated.getId().toString(),
                                updated.getName(),
                                updated.getStock()
                        )
                );

    }

    @Override
    public Mono<Void> deleteById(String productId) {
        return repository.deleteById(productId);
    }

    @Override
    public Mono<Product> findTopByBranchIdOrderByStockDesc(String branchId) {
        return repository.findFirstByBranchIdOrderByStockDesc(branchId)
                .map(entity -> new Product(entity.getId().toString(), entity.getName(), entity.getStock()));
    }

    @Override
    public Mono<Product> updateName(String id, String newName) {
        return repository.findById(id).flatMap(entity -> {
            entity.setName(newName);
            return repository.save(entity);
        }).map(updated -> new Product(updated.getId().toString(), updated.getName(),updated.getStock()));
    }
}
