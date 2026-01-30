package com.prueba.SETI.infrastructure.adapter.persistence.adapter;

import com.prueba.SETI.domain.model.franchise.Franchise;
import com.prueba.SETI.domain.model.franchise.gateway.FranchiseRepositoryPort;
import com.prueba.SETI.infrastructure.adapter.persistence.repository.FranchiseR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final FranchiseR2dbcRepository repository;
    private final DatabaseClient databaseClient;
    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return databaseClient.sql("""
                    INSERT INTO franchises (id, name)
                    VALUES (:id, :name)
                """)
                .bind("id", franchise.getId())
                .bind("name", franchise.getName())
                .fetch()
                .rowsUpdated()
                .map(rows -> new Franchise(franchise.getId(), franchise.getName()));

    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id)
                .map(entity -> new Franchise(entity.getId().toString(), entity.getName()));
    }

    @Override
    public Mono<Franchise> updateName(String id, String newName) {
        return repository.findById(id).flatMap(entity -> {
            entity.setName(newName);
            return repository.save(entity);
        }).map(updated -> new Franchise(updated.getId().toString(), updated.getName()));
    }
}
