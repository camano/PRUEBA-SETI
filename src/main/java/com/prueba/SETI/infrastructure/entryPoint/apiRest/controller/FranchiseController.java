package com.prueba.SETI.infrastructure.entryPoint.apiRest.controller;

import com.prueba.SETI.domain.model.franchise.Franchise;
import com.prueba.SETI.domain.useCase.AddFranchise.AddFranchiseUseCase;
import com.prueba.SETI.domain.useCase.updatenamefranchise.UpdateNameFranchiseUseCase;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.request.CreateFranchiseRequest;
import com.prueba.SETI.infrastructure.entryPoint.apiRest.request.UpdateNameRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchise")
@RequiredArgsConstructor
@Slf4j
public class FranchiseController {

    private final AddFranchiseUseCase franchiseService;
    private final UpdateNameFranchiseUseCase updateFranchiseName;


    @PostMapping("/add")
    public Mono<Franchise> createFranchise(@RequestBody CreateFranchiseRequest request) {
        log.info("Creando nueva franquicia [{}]", request.name());
        return franchiseService.execute(request)
                .doOnSuccess(f -> log.info("Franquicia creada [{}]", f.getId()))
                .doOnError(e -> log.error("Error creando franquicia", e));
    }

    @PatchMapping("/{franchiseId}/name")
    public Mono<Franchise> updateName(@PathVariable String franchiseId, @RequestBody UpdateNameRequest request) {
        log.info("Actualizando nombre de franquicia [{}]", franchiseId);

        return updateFranchiseName.execute(franchiseId, request.name())
                .doOnSuccess(f -> log.info("Nuevo nombre [{}] para franquicia [{}]", f.getName(), franchiseId))
                .doOnError(e -> log.error("Error actualizando franquicia", e));
    }

}
