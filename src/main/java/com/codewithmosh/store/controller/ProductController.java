package com.codewithmosh.store.controller;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.entities.Category;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import com.codewithmosh.store.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<ProductDto> getAllProducts(@RequestParam(name = "categoryId", required = false) Byte categotyId){
        List<Product> products;

        if (categotyId != null){
            products = productService.findByCategory(categotyId);
        } else {
            products = productService.findAll();
        }
        return products.stream().map(productMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        List<Product> products;
        Optional<Product> product = productService.findProductById(id);
        if (!product.isPresent()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(product.get()));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){
        Optional<Category> category = categoryRepository.findById(productDto.getCategoryId());
        if (!category.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        Product product = productMapper.toEntity(productDto);
        product.setCategory(category.get());
        productRepository.save(product);
        productDto.setId(product.getId());

        return ResponseEntity.ok(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto){
        Product product = productService.findProductById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }

        productMapper.updateDto(productDto, product);
        productRepository.save(product);

        productDto.setId(product.getId());
        return ResponseEntity.ok(productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable Long id){
        Product product = productService.findProductById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }

        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
