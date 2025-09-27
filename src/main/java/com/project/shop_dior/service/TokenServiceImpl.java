package com.project.shop_dior.service;

import com.project.shop_dior.models.Token;
import com.project.shop_dior.models.User;
import com.project.shop_dior.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable


    @Transactional
    @Override
    public Token addToken(User user, String token) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds);
        // Tạo mới một token cho người dùng
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .build();

        tokenRepository.save(newToken);
        return newToken;
    }
}
