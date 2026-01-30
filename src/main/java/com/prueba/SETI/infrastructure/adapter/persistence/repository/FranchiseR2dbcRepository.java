package com.prueba.SETI.infrastructure.adapter.persistence.repository;

import com.prueba.SETI.infrastructure.adapter.persistence.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface FranchiseR2dbcRepository extends ReactiveCrudRepository <FranchiseEntity, String>{
}
