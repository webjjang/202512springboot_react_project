package com.webjjang.api.service.impl;

import com.webjjang.api.data.dto.ProductDto;
import com.webjjang.api.data.dto.ProductResponseDto;
import com.webjjang.api.data.entity.Product;
import com.webjjang.api.data.repository.ProductRepository;
import com.webjjang.api.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 기본 패키지 아래에서 자동생성되게 하는 어노테이션
// @Controller, @Service, @Repository, @Component, @RestController, @~Advice
@Service
public class ProductServiceImpl implements ProductService {

    // 로그 출력을 위한 객체 선언
    private final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    // DB CRUD 직접 처리를 간절히 원한다. 필요한 클래스 ProductRepository
    private final ProductRepository productRepository;

    @Autowired // 자동 DI - 생성자를 이용해서 생성되어있는 ProductDAO 타입에 해당되는 객체를 전달 받는다.
    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    // 상품 상세 보기
    @Override
    public ProductResponseDto getProduct(Long number) {
        // 전달되는 데이터 확인
        LOGGER.info("[getProduct] input number : {}", number);

        // Product Entity로 DB에 맞춘다. -> 백엔드 개발자나 프론트 엔드 개발자의 자바 코드와는 상이하다고 본다.
        Product product = productRepository.getReferenceById(number);

        // DB 에서 가져온 데이터 확인
        LOGGER.info("[getProdec] prodect number : {}, name : {}", product.getName(), product.getName());

        // 표시하는 쪽에 객체에 담기 위해서 생성을 해야만다.
        ProductResponseDto productResponseDto = new ProductResponseDto();
        // Product -> ProductResponseDto에 저장한다.
        productResponseDto.setNumber(product.getNumber());
        productResponseDto.setName(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setStock(product.getStock());

        return productResponseDto;
    }

    // 상품 등록
    @Override
    public ProductResponseDto saveProduct(ProductDto productDto) {

        // 넘어오는 데이터 확인
        LOGGER.info("[saveProduct] productDTO : {}", productDto);

        // dao에서 사용하고 있는 Product 객체에 전달되는 ProductDto의 데이터 담기
        // 그래서 Product를 생성해서 담아야만 한다.
        Product product = new Product();

        // 데이터 담기 : productDto -> product에 담는다.
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStoke());
        // 등록일과 수정일 - 자바에서 처리함.
        // DB 처리하는 경우 - oracle : sysdate, MySQL/MariaDB : now()
        // product.setCreateAt(LocalDateTime.now());
        // product.setUpdateAt(LocalDateTime.now());

        // DB 처리
        Product savedProduct = productRepository.save(product);

        // 상품 등록한 내용 확인 - 로그로 출력
        LOGGER.info("[savedProduct] savedProduct : {}", savedProduct);

        // 표시하는 쪽에 객체에 담기 위해서 생성을 해야만다.
        ProductResponseDto productResponseDto = new ProductResponseDto();
        // Product -> ProductResponseDto에 저장한다.
        productResponseDto.setNumber(savedProduct.getNumber());
        productResponseDto.setName(savedProduct.getName());
        productResponseDto.setPrice(savedProduct.getPrice());
        productResponseDto.setStock(savedProduct.getStock());

        // 등록된 상품의 정보 리턴
        return productResponseDto;
    }

    // 상품명 수정 - 수정된 정보를 보내준다.
    @Override
    public ProductResponseDto changeProductName(Long number, String name) throws Exception {
        Optional<Product> selectedProduct = productRepository.findById(number);
        Product foundProduct = null;

        if(selectedProduct.isPresent()) {
            foundProduct = selectedProduct.get(); // 상품 번호에 맞는 데이터 가져오기
            foundProduct.setName(name); // 상품명 바꾸기
            Product changedProduct = productRepository.save(foundProduct); // 바꿔진 데이터를 저장

            // 표시하는 쪽에 객체에 담기 위해서 생성을 해야만다.
            ProductResponseDto productResponseDto = new ProductResponseDto();
            // Product -> ProductResponseDto에 저장한다.
            productResponseDto.setNumber(changedProduct.getNumber());
            productResponseDto.setName(changedProduct.getName());
            productResponseDto.setPrice(changedProduct.getPrice());
            productResponseDto.setStock(changedProduct.getStock());
            return productResponseDto;
        }
        throw new Exception();
    }

    // 상품 삭제
    @Override
    public void deleteProduct(Long number) throws Exception {

        // DB 에서 상품 삭제
        productRepository.deleteById(number);

    }

}
