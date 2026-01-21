package com.prueba.SETI.application.service;

import com.prueba.SETI.application.api.request.CreateFranchiseRequest;
import com.prueba.SETI.application.exception.NotFoundException;
import com.prueba.SETI.domain.model.Franchise;
import com.prueba.SETI.domain.ports.FranchiseRepositoryPort;
import com.prueba.SETI.domain.ports.SequenceGeneratorPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseService {

    private final FranchiseRepositoryPort franchiseRepositoryPort;
    private final SequenceGeneratorPort sequenceGeneratorPort;

    public Mono<Franchise> execute(CreateFranchiseRequest request) {

        return sequenceGeneratorPort.nextFranchiseId()
                .doOnSubscribe(s -> log.info("Creando nueva franquicia [{}]", request.name()))
                .map(franchiseId-> new Franchise(franchiseId, request.name()))
                .flatMap(franchiseRepositoryPort::save)
                .doOnNext(franchise -> log.info("Franquicia creada con id [{}]", franchise.getId()))
                .doOnError(error -> log.error("Error creando franquicia", error));
    }


    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("Franchise name cannot be empty"));
        }

        return franchiseRepositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .flatMap(franchise -> franchiseRepositoryPort.updateName(franchiseId, newName))
                .doOnSubscribe(s -> log.info("Actualizando nombre de franquicia [{}]", franchiseId))
                .doOnNext(f -> log.info("Nuevo nombre [{}] para franquicia [{}]", f.getName(), franchiseId))
                .doOnError(e -> log.error("Error actualizando franquicia", e));
    }
}
