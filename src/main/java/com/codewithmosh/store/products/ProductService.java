package com.codewithmosh.store.products;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAllWithCategory();
    }

    public List<Product> findByCategory(Byte categoryId) {
        return productRepository.findByCategory_Id(categoryId);
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

}
