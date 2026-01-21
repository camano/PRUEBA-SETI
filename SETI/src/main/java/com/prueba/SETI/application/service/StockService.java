package com.prueba.SETI.application.service;

import com.prueba.SETI.application.api.response.ProductStockResponse;
import com.prueba.SETI.domain.model.Branch;
import com.prueba.SETI.domain.model.Product;
import com.prueba.SETI.domain.ports.BranchRepositoryPort;
import com.prueba.SETI.domain.ports.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {
    private final BranchRepositoryPort branchRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public Flux<ProductStockResponse> getTopStockByFranchise(String franchiseId) {

        return branchRepositoryPort.findByFranchiseId(franchiseId)
                .flatMap(branch -> Mono.zip(Mono.just(branch), productRepositoryPort.findTopByBranchIdOrderByStockDesc(branch.getId()).switchIfEmpty(Mono.empty())))
                .map(tuple -> {
                    Branch branch = tuple.getT1();
                    Product product = tuple.getT2();
                    return new ProductStockResponse(branch.getId(), branch.getName(), product.getId(), product.getName(), product.getStock());
                })
                .doOnSubscribe(s -> log.info("Consultando producto con mayor stock para franquicia [{}]", franchiseId))
                .doOnComplete(() -> log.info("Consulta finalizada"));
    }
}
