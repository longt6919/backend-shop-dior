package com.project.shop_dior.repository;

import com.project.shop_dior.models.Token;
import com.project.shop_dior.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token,Long> {
    List<Token> findByUser(User user);
}
