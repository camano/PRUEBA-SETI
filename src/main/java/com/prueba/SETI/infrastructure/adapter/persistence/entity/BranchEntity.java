package com.prueba.SETI.infrastructure.adapter.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("branches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchEntity {

    @Id
    private String id;
    private String name;
    private String franchiseId;
}
