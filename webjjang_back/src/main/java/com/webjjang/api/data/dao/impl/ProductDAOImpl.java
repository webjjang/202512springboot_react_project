package com.webjjang.api.data.dao.impl;

import com.webjjang.api.data.dao.ProductDAO;
import com.webjjang.api.data.entity.Product;
import com.webjjang.api.data.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

// 자동 생성 어노테이션 - 기본 패키지 안에 존재해야만 한다.
// - @Controller, @RestController, @Service, @Repository, @Component, @~Advice
@Component
public class ProductDAOImpl implements ProductDAO {

    // DAO가 JPA 방식으로 DB 처리를 할 때 Repository 간절히 필요하다.
    private final ProductRepository productRepository;

    // 생성자나 setter를 만든다. @Autowird 선언해 놓는다.
    // 생성자는 변수에 초기값 세팅을 위해서 만든다.
    @Autowired // 자동 DI
    public ProductDAOImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 상품 등록
    @Override // 상속 받은 메서드이다.
    public Product insertProduct(Product product) {
        // repository의 save()는 insert나 update 처리를 한다.
        // id 데이터가 있으면 update가 없으면 insert 처리를 해준다.
        // Product saveProduct = productRepository.save(product);
        return productRepository.save(product);
    }

    // 상품 상세 보기
    @Override // 상속 받은 메서드이다.
    public Product selectProduct(Long number) {
        // 교제 - getById() : 사양코드 -> getReferenceById() 사용하세요.
        return productRepository.getReferenceById(number);
    }

    // 상품명 변경하기
    @Override // 상속 받은 메서드이다.
    public Product updateProductName(Long number, String name) throws Exception {
        // 상품이 있는 확인해보자.
        Optional<Product> seletedProduct = productRepository.findById(number);

        Product updatedProduct;

        // DB 안에 데이터를 꺼냈는데 데이터가 존재하면 true가 나온다.
        if(seletedProduct.isPresent()){
            Product product = seletedProduct.get(); // 꺼내온 데이터를 변수에 저장해 놓는다.
            // 이름 바꾼다.
            product.setName(name);
            // 수정 날짜와 시간을 세팅한다. updateAt
            // 현재 날짜와 시간은 updateAt에 세팅했다.(JAVA에서 처리)->mariaDB: now()
            product.setUpdateAt(LocalDateTime.now());

            // save() - 데이터가 존재하면 update, 아니면 insert 처리한다.
            updatedProduct = productRepository.save(product);
        } else {
            throw new Exception(); // 수정할 데이터가 없으면 예외라고 본다.
        }
        return updatedProduct;
    }

    // 상품 삭제
    @Override // 상속 받은 메서드이다.
    public void deleteProduct(Long number) throws Exception {
        // 상품이 있는 확인해보자.
        Optional<Product> seletedProduct = productRepository.findById(number);

        // DB 안에 데이터를 꺼냈는데 데이터가 존재하면 true가 나온다.
        if(seletedProduct.isPresent()){
            Product product = seletedProduct.get(); // 꺼내온 데이터를 변수에 저장해 놓는다.

            // delete() - 데이터가 존재하면 update, 아니면 insert 처리한다.
            productRepository.delete(product);
        } else {
            throw new Exception(); // 삭제할 데이터가 없으면 예외라고 본다.
        }
    }
}
