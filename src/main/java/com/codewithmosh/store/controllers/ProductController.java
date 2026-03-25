package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public Iterable<ProductDto> getAllProducts(
            @RequestParam(required = false, name = "categoryId") Byte categoryId
    ) {

        List<Product> products;
        if (categoryId == null) {
            products = productRepository.findAllWithCategory();
        }else{
            products = productRepository.findByCategoryId(categoryId);
        }
    return products
            .stream()
            .map(productMapper::ToDto)
            .toList();
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDto> getProductId(@PathVariable Long id) {
    var product = productRepository.findById(id).orElse(null);
    if (product == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(productMapper.ToDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(
            @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder
    ){
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }


        var product =  productMapper.ToEntity(productDto);
        product.setCategory(category);
        productRepository.save(product);

        var uri = uriBuilder.path("/products/{id}").buildAndExpand(productDto.getId()).toUri();
        return ResponseEntity.created(uri).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto
    ){
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        var product = productRepository.findById(id).orElse(null);
        if(product == null){
            return ResponseEntity.notFound().build();
        }

        productMapper.update(productDto, product);
        product.setCategory(category);
        productRepository.save(product);
        product.setId(id);

        return ResponseEntity.ok(productMapper.ToDto(product));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        var product = productRepository.findById(id).orElse(null);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);

        return ResponseEntity.ok().build();
    }
}
