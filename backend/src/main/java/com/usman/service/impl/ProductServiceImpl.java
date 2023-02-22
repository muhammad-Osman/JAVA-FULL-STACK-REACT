package com.usman.service.impl;

import com.usman.models.Product;
import com.usman.repository.ProductRepository;
import com.usman.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(Product product) {
        Product result = productRepository.save(product);
        return result;
    }

    @Override
    public String deleteProduct(Long productId) {
        productRepository.deleteById(productId);
        return "Product deleted successfully!";
    }

    @Override
    public List<Product> fetchAllProducts(Integer pageNo, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);

        Page<Product> productList = productRepository.findAll(paging);

        if(productList.hasContent()) {
            return productList.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Product> fetchProductById(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product updateProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found Tutorial with id = " + id));

        updateProduct.setProductName(product.getProductName());
        updateProduct.setPrice(product.getPrice());
        updateProduct.setQuantity(product.getQuantity());

        return productRepository.save(updateProduct);
    }

}
