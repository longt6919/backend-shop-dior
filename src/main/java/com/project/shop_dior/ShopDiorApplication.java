package com.project.shop_dior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShopDiorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopDiorApplication.class, args);
	}

}
