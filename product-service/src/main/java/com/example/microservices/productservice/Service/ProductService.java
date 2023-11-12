package com.example.microservices.productservice.Service;

import com.example.microservices.productservice.DTO.ProductRequest;
import com.example.microservices.productservice.DTO.ProductResponse;
import com.example.microservices.productservice.Model.Product;
import com.example.microservices.productservice.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::MapToProductResponse).toList();
    }

    private ProductResponse MapToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();


        productRepository.save(product);
        log.info("Product {} is saved" , product.getId());
    }



}
