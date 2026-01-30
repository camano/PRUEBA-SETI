package com.prueba.SETI.domain.useCase.addproduct;


import com.prueba.SETI.domain.model.Sequence.gateway.SequenceGeneratorPort;
import com.prueba.SETI.domain.model.branch.gateway.BranchRepositoryPort;
import com.prueba.SETI.domain.model.product.Product;
import com.prueba.SETI.domain.model.product.gateway.ProductRepositoryPort;
import com.prueba.SETI.infrastructure.helper.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AddProductUseCase {
    private final BranchRepositoryPort branchRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final SequenceGeneratorPort sequenceGeneratorPort;

    public Mono<Product> execute(String branchId, String name, int stock) {

        return branchRepositoryPort.findById(branchId)
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(branch ->
                        sequenceGeneratorPort.nextProductId()
                                .map(productId -> new Product(productId, name, stock))
                                .flatMap(product -> productRepositoryPort.save(branchId, product))
                );
    }
}
