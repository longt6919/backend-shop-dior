package com.project.shop_dior.service;

import com.project.shop_dior.models.Token;
import com.project.shop_dior.models.User;

public interface TokenService {
    Token addToken(User user, String token);

}
