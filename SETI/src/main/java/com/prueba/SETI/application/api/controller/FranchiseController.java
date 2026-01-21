package com.prueba.SETI.application.api.controller;

import com.prueba.SETI.application.api.request.CreateFranchiseRequest;
import com.prueba.SETI.application.api.request.UpdateNameRequest;
import com.prueba.SETI.application.service.FranchiseService;
import com.prueba.SETI.domain.model.Franchise;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchise")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;


    @PostMapping("/add")
    public Mono<Franchise> createFranchise(@RequestBody CreateFranchiseRequest request) {
        return franchiseService.execute(request);
    }

    @PatchMapping("/{franchiseId}/name")
    public Mono<Franchise> updateName(@PathVariable String franchiseId, @RequestBody UpdateNameRequest request) {
        return franchiseService.updateFranchiseName(franchiseId, request.name());
    }

}
