package co.com.bancolombia.api.controller;


import co.com.bancolombia.api.request.CreateBranchRequest;
import co.com.bancolombia.api.request.UpdateNameRequest;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.usecase.addbranch.AddBranchUseCase;
import co.com.bancolombia.usecase.updatenamebranch.UpdateNameBranchUseCase;
import co.com.bancolombia.usecase.updatenamefranchise.UpdateNameFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/branch")
@RequiredArgsConstructor
@Slf4j
public class branchController {

    private final AddBranchUseCase addBranchUseCase;
    private final UpdateNameBranchUseCase updateNameBranchUseCase;

    @PostMapping("/add/{franchiseId}/branches")
    public Mono<Branch> createBranch(
            @PathVariable String franchiseId,
            @RequestBody CreateBranchRequest request) {

        log.info("Creando sucursal [{}] para franquicia [{}]", request.name(), franchiseId);

        return addBranchUseCase.execute(franchiseId, request.name())
                .doOnSuccess(branch -> log.info("Sucursal creada con id [{}]", branch.getId()))
                .doOnError(e -> log.error("Error creando sucursal", e));
    }

    @PatchMapping("/{branchId}/name")
    public Mono<Branch> updateName(@PathVariable String branchId, @RequestBody UpdateNameRequest request) {
        log.info("Actualizando nombre de sucursal [{}]", branchId);

        return updateNameBranchUseCase.execute(branchId, request.name())
                .doOnSuccess(b -> log.info("Nuevo nombre [{}] para sucursal [{}]", b.getName(), branchId))
                .doOnError(e -> log.error("Error actualizando sucursal", e));

    }
}
