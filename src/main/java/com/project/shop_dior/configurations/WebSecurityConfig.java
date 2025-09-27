package com.project.shop_dior.configurations;
import com.project.shop_dior.filters.JwtTokenFilter;
import com.project.shop_dior.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private static final String API_GET_ALL_USER = "/api/v1/users";
    private static final String API_REGISTER = "/api/v1/users/register";
    private static final String API_LOGIN = "/api/v1/users/login";
    private static final String API_ORDER = "/api/v1/orders/**";
    private static final String API_ORDER_BY_KEYWORD = "/api/v1/orders/get-orders-by-keyword";
    private static final String API_ORDER_POST = "/api/v1/orders";
    private static final String API_CATEGORIES = "/api/v1/categories/**";
    private static final String API_PRODUCTS = "/api/v1/products/**";
    private static final String API_BRANDS = "/api/v1/brand/**";
    private static final String API_ORDER_DETAIL = "/api/v1/order_detail/**";
    private static final String API_ORDER_DETAIL_BY_ID_ORDER = "/api/v1/order_detail/order/**";
    private static final String API_ROLE = "/api/v1/roles";
    private static final String API_SIZE = "/api/v1/size";
    private static final String API_COLOR = "/api/v1/color";
    private static final String API_MATERIAL = "/api/v1/material";
    private static final String API_MATERIAL_BY_ID = "/api/v1/material/**";
    private static final String API_PRODUCT_IMAGE = "/api/v1/products/images/**";
    private static final String API_USER_DETAIL = "/api/v1/users/details";
    private static final String API_PRODUCT_UPLOAD = "/api/v1/products/uploads/**";
    private static final String API_COUPONS = "/api/v1/coupons/**";
    private static final String API_AUTH = "/api/v1/users/auth/social-login";
    private static final String API_AUTH_CALLBACK = "/api/v1/users/auth/social/callback/**";
    private static final String ERROR = "error";
    private static final String API_PAYMENT_VNP = "/api/v1/payments/create_payment_url";
    private static final String API_STYLES = "/api/v1/styles/**";
    private static final String API_PRODUCT_DETAIL = "/api/v1/productDetail/product/**";
    private static final String API_PRODUCTS_ADMIN = "/api/v1/products/home/admin";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(API_REGISTER,API_LOGIN,API_ROLE,API_PRODUCTS,API_COLOR,API_MATERIAL,
                                        API_CATEGORIES,API_USER_DETAIL,API_ORDER_POST,API_PAYMENT_VNP,
                                        API_PRODUCT_UPLOAD,API_PRODUCT_IMAGE,API_AUTH,API_SIZE,API_MATERIAL,
                                        API_MATERIAL_BY_ID,API_STYLES,API_BRANDS
                                        ,API_AUTH_CALLBACK,API_COUPONS,API_ORDER,ERROR)
                                .permitAll()
                                .requestMatchers(POST, API_MATERIAL).hasAuthority(Role.ADMIN)
                                .requestMatchers(PUT, API_MATERIAL_BY_ID).hasAuthority(Role.ADMIN)
                                .requestMatchers(DELETE, API_MATERIAL_BY_ID).hasAuthority(Role.ADMIN)
                                //----------------------------------------------------------------------//
                                .requestMatchers(GET,API_PRODUCT_DETAIL).hasAuthority(Role.ADMIN)
                                //----------------------------------------------------------------------//
                                .requestMatchers(GET, API_GET_ALL_USER).hasAnyAuthority(Role.ADMIN)
                                //----------------------------------------------------------------------//
                                .requestMatchers(POST, API_CATEGORIES).hasAuthority(Role.ADMIN)
                                .requestMatchers(PUT, API_CATEGORIES).hasAuthority(Role.ADMIN)
                                .requestMatchers(DELETE, API_CATEGORIES).hasAuthority(Role.ADMIN)
                                //----------------------------------------------------------------------//
//                                .requestMatchers(DELETE, API_ORDER).hasAuthority(Role.ADMIN)
                                .requestMatchers(GET, API_ORDER_BY_KEYWORD).hasAnyAuthority(Role.ADMIN,Role.EMPLOYEE)
//                                .requestMatchers(PUT, API_ORDER_PUT_STATUS).hasAuthority(Role.USER)
//                                .requestMatchers(PUT,API_ORDER).hasRole("ADMIN") nếu CSDL là ADMIN
//                                .requestMatchers(POST, API_ORDER_POST).hasAuthority(Role.USER)
                                //----------------------------------------------------------------------//
                                .requestMatchers(POST, API_PRODUCTS).hasAuthority(Role.ADMIN)
                                .requestMatchers(PUT, API_PRODUCTS).hasAuthority(Role.ADMIN)
                                .requestMatchers(DELETE, API_PRODUCTS).hasAuthority(Role.ADMIN)
                                .requestMatchers(GET, API_PRODUCTS_ADMIN).hasAuthority(Role.ADMIN)
                                //----------------------------------------------------------------------//
                                .requestMatchers(GET, API_ORDER_DETAIL).hasAnyAuthority(Role.USER, Role.ADMIN)
                                .requestMatchers(GET, API_ORDER_DETAIL_BY_ID_ORDER).hasAnyAuthority(Role.ADMIN, Role.USER)
                                .requestMatchers(POST, API_ORDER_DETAIL).hasAuthority(Role.USER)
                                .requestMatchers(PUT, API_ORDER_DETAIL).hasAnyAuthority(Role.ADMIN,Role.EMPLOYEE)
                                .requestMatchers(DELETE, API_ORDER_DETAIL).hasAuthority(Role.ADMIN)
                                //----------------------------------------------------------------------//
                                .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable);
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
                    @Override
                    public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(List.of("*"));
                        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                        configuration.setAllowedHeaders(Arrays.asList("authorization","content-type","x-auth-token"));
                        configuration.setExposedHeaders(List.of("x-auth-token"));
                        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                        source.registerCorsConfiguration("/**",configuration);
                        httpSecurityCorsConfigurer.configurationSource(source);
                    }
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
