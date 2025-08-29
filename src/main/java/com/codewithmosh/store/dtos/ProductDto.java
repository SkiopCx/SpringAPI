package com.codewithmosh.store.dtos;

import com.codewithmosh.store.entities.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private byte categoryId;
}
