package com.prueba.SETI.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupCheck {

    private final DatabaseClient databaseClient;

    @PostConstruct
    public void check() {
        databaseClient.sql("SELECT 1")
                .fetch()
                .one()
                .doOnNext(r -> System.out.println("DB conectada ✔"))
                .subscribe();
    }
}

