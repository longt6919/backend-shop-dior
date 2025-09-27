package com.project.shop_dior.service;

import com.project.shop_dior.dtos.UpdateUserDTO;
import com.project.shop_dior.dtos.UserDTO;
import com.project.shop_dior.dtos.UserLoginDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.exception.InvalidPasswordException;
import com.project.shop_dior.models.Color;
import com.project.shop_dior.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password,Long roleId) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User getUserById(long id);
    User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws DataNotFoundException;
    String loginSocial(UserLoginDTO userLoginDTO) throws Exception;
    Page<User> findAll(String keyword, Pageable pageable) throws Exception;
    Page<User> findAllEmployee(String keyword, Pageable pageable) throws Exception;
    void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException ;
    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
    User updateEmployee(long userId, UserDTO userDTO);

}
