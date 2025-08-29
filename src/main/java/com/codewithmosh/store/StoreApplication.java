package com.codewithmosh.store;

import com.codewithmosh.store.repositories.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class StoreApplication {

    public static void main(String[] args) {

        SpringApplication.run(StoreApplication.class, args);

//        ApplicationContext context = SpringApplication.run(StoreApplication.class, args);
//        var bean = context.getBean(ProductRepository.class);
//        var dd = bean.findByCategory_Id((byte) 1);
//        dd.stream().forEach(d -> {
//            System.out.println(d.getId());
//        });
    }
}
