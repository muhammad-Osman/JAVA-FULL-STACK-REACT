package com.usman.controller;

import com.usman.models.Product;
import com.usman.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/addProduct")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product result = productService.addProduct(product);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> fetchProductById(@PathVariable("id") Long id) {
        Product product = productService.fetchProductById(id).orElseThrow(
                () -> new NotFoundException("product with id: " + id));

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<List<Product>> fetchAllProducts( @RequestParam(defaultValue = "0") Integer pageNo,
                                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Product> merchantList = productService.fetchAllProducts(pageNo, pageSize);

        if(merchantList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(merchantList, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id, @RequestBody Product product){
        Product updateMerchant = productService.updateProduct(id, product);

        return new ResponseEntity<>(updateMerchant, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        String result = productService.deleteProduct(id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
