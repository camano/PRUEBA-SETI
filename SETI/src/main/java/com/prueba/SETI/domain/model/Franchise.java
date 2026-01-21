package com.prueba.SETI.domain.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Franchise {

    private final String id;
    private final String name;
    private final List<Branch> branches;

    public Franchise(String id, String name) {
        this.id = id;
        this.name = name;
        this.branches = new ArrayList<>();
    }

    public void addBranch(Branch branch) {
        this.branches.add(branch);
    }
}
