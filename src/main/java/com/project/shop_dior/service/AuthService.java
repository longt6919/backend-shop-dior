package com.project.shop_dior.service;

import java.io.IOException;
import java.util.Map;

public interface AuthService {
    String generateAuthUrl(String loginType);
    Map<String, Object> authenticateAndFetchProfile(String code, String loginType) throws IOException;
}
