package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductR2dbcRepository
        extends ReactiveCrudRepository<ProductEntity, String> {

    Mono<ProductEntity> findFirstByBranchIdOrderByStockDesc(String branchId);
}
