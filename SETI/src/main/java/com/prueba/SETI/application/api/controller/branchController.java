package com.prueba.SETI.application.api.controller;

import com.prueba.SETI.application.api.request.CreateBranchRequest;
import com.prueba.SETI.application.api.request.UpdateNameRequest;
import com.prueba.SETI.application.service.BranchService;
import com.prueba.SETI.domain.model.Branch;
import com.prueba.SETI.domain.model.Franchise;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/branch")
@RequiredArgsConstructor
public class branchController {

    private final BranchService branchService;

    @PostMapping("/add/{franchiseId}/branches")
    public Mono<Branch> createBranch(
            @PathVariable String franchiseId,
            @RequestBody CreateBranchRequest request) {

        return branchService.execute(franchiseId, request.name());
    }

    @PatchMapping("/{branchId}/name")
    public Mono<Branch> updateName(@PathVariable String branchId, @RequestBody UpdateNameRequest request) {
        return branchService.updateBranchName(branchId, request.name());
    }
}
