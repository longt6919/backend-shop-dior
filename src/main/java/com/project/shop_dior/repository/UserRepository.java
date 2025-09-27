package com.project.shop_dior.repository;

import com.project.shop_dior.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    @Query("SELECT o FROM User o WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE %:keyword% " +
            "OR o.address LIKE %:keyword% " +
            "OR o.phoneNumber LIKE %:keyword%) " +
            "AND LOWER(o.role.name) = 'user'")
    Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT o FROM User o WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE %:keyword% " +
            "OR o.address LIKE %:keyword% " +
            "OR o.phoneNumber LIKE %:keyword%) " +
            "AND LOWER(o.role.name) = 'employee'")
    Page<User> findAllEmployee(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
    SELECT r.name, COUNT(u.id)
      FROM User u
      JOIN u.role r
     WHERE r.name IN :names
     GROUP BY r.name
""")
    List<Object[]> countUsersByRoleNames(@Param("names") List<String> names);

    Optional<User> findByFacebookAccountId(String facebookAccountId);
    Optional<User> findByGoogleAccountId(String googleAccountId);
}
