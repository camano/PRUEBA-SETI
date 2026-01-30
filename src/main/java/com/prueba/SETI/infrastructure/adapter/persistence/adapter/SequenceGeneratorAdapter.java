package com.prueba.SETI.infrastructure.adapter.persistence.adapter;

import com.prueba.SETI.domain.model.Sequence.gateway.SequenceGeneratorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SequenceGeneratorAdapter
        implements SequenceGeneratorPort {

    private final DatabaseClient databaseClient;



    @Override
    public Mono<String> nextFranchiseId() {
        return next("franchise", "f-");
    }

    @Override
    public Mono<String> nextBranchId() {
        return next("branch", "b-");
    }

    @Override
    public Mono<String> nextProductId() {
        return next("product", "p-");
    }

    private Mono<String> next(String name, String prefix) {

        return databaseClient.sql("""
            UPDATE sequences
            SET value = value + 1
            WHERE name = :name
            RETURNING value
        """)
                .bind("name", name)
                .map(row -> row.get("value", Long.class))
                .one()
                .map(value -> prefix + value);
    }
}
