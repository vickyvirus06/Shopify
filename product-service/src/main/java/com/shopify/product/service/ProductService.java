package com.shopify.product.service;

import com.shopify.product.dto.ProductDTO;

import java.util.List;


public interface ProductService {

    void createProduct(ProductDTO productDTO);

    List<ProductDTO> getAllProducts();
}

