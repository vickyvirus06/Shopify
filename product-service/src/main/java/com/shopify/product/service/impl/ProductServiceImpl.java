package com.shopify.product.service.impl;

import com.shopify.product.dto.ProductDTO;
import com.shopify.product.entity.Product;
import com.shopify.product.repository.ProductRepository;
import com.shopify.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public void createProduct(ProductDTO productDTO) {

        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getName());
        }

    @Override
    public List<ProductDTO> getAllProducts() {
       List<Product> products = productRepository.findAll();

       return products.stream().map(this::mapToProductDTO).collect(Collectors.toList());
    }

    private ProductDTO mapToProductDTO(Product product) {
        return ProductDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
