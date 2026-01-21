package com.prueba.SETI.application.service;

import com.prueba.SETI.application.exception.NotFoundException;
import com.prueba.SETI.domain.model.Branch;
import com.prueba.SETI.domain.model.Product;
import com.prueba.SETI.domain.ports.BranchRepositoryPort;
import com.prueba.SETI.domain.ports.FranchiseRepositoryPort;
import com.prueba.SETI.domain.ports.SequenceGeneratorPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {
    private final FranchiseRepositoryPort franchiseRepository;
    private final BranchRepositoryPort branchRepository;
    private final SequenceGeneratorPort sequenceGeneratorPort;

    public Mono<Branch> execute(String franchiseId, String branchName) {

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .doOnSubscribe(s -> log.info("Creando sucursal [{}] para franquicia [{}]", branchName, franchiseId))
                .flatMap(franchise ->
                        sequenceGeneratorPort.nextBranchId()
                                .map(branchId ->
                                        new Branch(branchId, branchName)
                                )
                                .flatMap(branch ->
                                        branchRepository.save(franchiseId, branch)
                                )
                )
                .doOnNext(branch ->log.info("Sucursal creada con id [{}]", branch.getId()))
                .doOnError(error -> log.error("Error creando sucursal", error));
    }

    public Mono<Branch> updateBranchName(String branchId, String newName) {

        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("sucursal name cannot be empty"));
        }

        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new NotFoundException("sucursal not found")))
                .flatMap(franchise -> branchRepository.updateName(branchId, newName))
                .doOnSubscribe(s -> log.info("Actualizando nombre de sucursal [{}]", branchId))
                .doOnNext(f -> log.info("Nuevo nombre [{}] para sucursal [{}]", f.getName(), branchId))
                .doOnError(e -> log.error("Error actualizando sucursal", e));
    }
}
