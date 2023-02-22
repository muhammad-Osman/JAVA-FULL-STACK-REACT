package com.usman.service;

import com.usman.models.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    public Product updateProduct(Long id, Product product);
    public Optional<Product> fetchProductById(Long productId);
    public List<Product> fetchAllProducts(Integer pageNo, Integer pageSize);
    public Product addProduct(Product product);
    public String deleteProduct(Long productId);
}
