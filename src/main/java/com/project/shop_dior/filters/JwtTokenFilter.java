package com.project.shop_dior.filters;

import com.project.shop_dior.component.JwtTokenUtil;
import com.project.shop_dior.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private static final List<Map.Entry<String, String>> BYPASS_TOKENS = Arrays.asList(
            new AbstractMap.SimpleEntry<>("api/v1/products/**", "GET"),
            new AbstractMap.SimpleEntry<>("api/v1/categories/**", "GET"),
            new AbstractMap.SimpleEntry<>("api/v1/users/register", "POST"),
            new AbstractMap.SimpleEntry<>("api/v1/users/login", "POST"),
            new AbstractMap.SimpleEntry<>("api/v1/products/images/**", "GET"),
            new AbstractMap.SimpleEntry<>("api/v1/roles", "GET")

            );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (isBypassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            final String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User existingUserDetail = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if (jwtTokenUtil.validateToken(token,existingUserDetail)){
                    System.out.println("User roles: " + existingUserDetail.getAuthorities());
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(existingUserDetail,null,existingUserDetail.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request,response);
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return BYPASS_TOKENS.stream()
                .anyMatch(token ->
                        pathMatcher.match("/" + token.getKey(), requestPath)
                                && requestMethod.equalsIgnoreCase(token.getValue()));
    }
}
