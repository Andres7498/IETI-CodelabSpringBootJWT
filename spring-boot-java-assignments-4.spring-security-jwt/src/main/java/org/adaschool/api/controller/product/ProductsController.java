package org.adaschool.api.controller.product;

import org.adaschool.api.exception.ProductNotFoundException;
import org.adaschool.api.repository.product.Product;
import org.adaschool.api.repository.product.ProductDto;
import org.adaschool.api.service.product.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/products/")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(@Autowired ProductsService productsService) {
        this.productsService = productsService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto) {
        var savedProduct = productsService.save(new Product(productDto));
        URI createdProductUri = URI.create("/v1/products/" + savedProduct.getId());
        return ResponseEntity.created(createdProductUri).body(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productsService.all();
        return ResponseEntity.ok(products);
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") String id) {
        Optional<Product> optionalProduct = productsService.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException(id);
        }
        return ResponseEntity.ok(optionalProduct.get());
    }

    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") String id, @RequestBody ProductDto ProductDto) {
        Optional<Product> existingProduct = productsService.findById(id);
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException(id);
        }
        Product product = existingProduct.get();
        product.update(ProductDto);
        Product updatedProduct = productsService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
        Optional<Product> existingProduct = productsService.findById(id);
        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException(id);
        }
        productsService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
