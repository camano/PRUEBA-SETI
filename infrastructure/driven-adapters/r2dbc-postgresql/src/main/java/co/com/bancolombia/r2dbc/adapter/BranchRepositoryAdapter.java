package co.com.bancolombia.r2dbc.adapter;


import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.r2dbc.repository.BranchR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepository {


    private final BranchR2dbcRepository repository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Branch> save(String franchiseId, Branch branch) {

        return databaseClient.sql("""
            INSERT INTO branches (id, name, franchise_id)
            VALUES (:id, :name, :franchiseId)
        """)
                .bind("id", branch.getId())
                .bind("name", branch.getName())
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .map(rows ->
                        new Branch(branch.getId(), branch.getName())
                );
    }

    @Override
    public Mono<Branch> findById(String id) {
        return repository.findById(id)
                .map(entity -> new Branch(entity.getId().toString(), entity.getName()));
    }

    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        return repository.findByFranchiseId(franchiseId)
                .map(entity -> new Branch(entity.getId().toString(), entity.getName()));
    }

    @Override
    public Mono<Branch> updateName(String id, String newName) {
        return repository.findById(id).flatMap(entity -> {
            entity.setName(newName);
            return repository.save(entity);
        }).map(updated -> new Branch(updated.getId().toString(), updated.getName()));
    }

}
