package com.prueba.SETI.domain.model.product;

import lombok.Getter;

@Getter
public class Product {
    private final String id;
    private final String name;
    private int stock;

    public Product(String id, String name, int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock no puede se menor a 0");
        }
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public void updateStock(int stock) {
        this.stock = stock;
    }
}
