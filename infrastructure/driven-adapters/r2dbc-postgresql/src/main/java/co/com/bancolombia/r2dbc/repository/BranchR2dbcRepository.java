package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BranchR2dbcRepository extends ReactiveCrudRepository<BranchEntity, String> {

    Flux<BranchEntity> findByFranchiseId(String franchiseId);
}
