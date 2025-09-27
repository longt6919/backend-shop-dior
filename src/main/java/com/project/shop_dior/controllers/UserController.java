package com.project.shop_dior.controllers;

import com.project.shop_dior.dtos.ColorDTO;
import com.project.shop_dior.dtos.UpdateUserDTO;
import com.project.shop_dior.dtos.UserDTO;
import com.project.shop_dior.dtos.UserLoginDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Color;
import com.project.shop_dior.models.Token;
import com.project.shop_dior.models.User;
import com.project.shop_dior.responses.*;
import com.project.shop_dior.service.AuthService;
import com.project.shop_dior.service.TokenService;
import com.project.shop_dior.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> createUser
            (@Valid @RequestBody UserDTO userDTO,
             BindingResult result) {
        RegisterResponse registerResponse = new RegisterResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            registerResponse.setMessage(errorMessages.toString());
            return ResponseEntity.badRequest().body(registerResponse);
        }
        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            registerResponse.setMessage("Mật khẩu không khớp");
            return ResponseEntity.badRequest().body(registerResponse);
        }
        try {
            User user = userService.createUser(userDTO);
            registerResponse.setMessage("Đăng kí tài khoản thành công");
            registerResponse.setUser(user);
            return ResponseEntity.ok(registerResponse);
        } catch (Exception e) {
            e.printStackTrace();
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO) throws Exception {
        // kiểm tra thông tin đăng nhạp và sinh token
            String token = userService.login(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId());
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Đăng nhập thành công")
                    .token(token).build());

    }
    @PostMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable("id") Long userId
    ){
        try {
            User existingUser = userService.getUserById(userId);
            return ResponseEntity.ok(UserResponse.fromUser(existingUser));
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UpdateResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO
    ) throws DataNotFoundException {
        UpdateResponse updateResponse = new UpdateResponse();
        userService.updateEmployee(id, userDTO);
        updateResponse.setMessage("Cập nhật thông tin thành công");
        return ResponseEntity.ok(updateResponse);
    }
    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUserDetail(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ){
        try{
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            if (user.getId() != userId){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();//chỉ chỉnh user đó mới sửa được tt user đó
            }
            User updateUser = userService.updateUser(userId,updatedUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updateUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getAllUser(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        try {
            // Tạo Pageable từ thông tin trang và giới hạn
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    //Sort.by("createdAt").descending()
                    Sort.by("id").ascending()
            );
            Page<UserResponse> userPage = userService.findAll(keyword, pageRequest)
                    .map(UserResponse::fromUser);
            // Lấy tổng số trang
            int totalPages = userPage.getTotalPages();
            List<UserResponse> userResponses = userPage.getContent();
            return ResponseEntity.ok(UserListResponse
                    .builder()
                    .users(userResponses)
                    .totalPages(totalPages)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/employee")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getAllEmployee(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        try {
            // Tạo Pageable từ thông tin trang và giới hạn
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    //Sort.by("createdAt").descending()
                    Sort.by("id").ascending()
            );
            Page<UserResponse> userPage = userService.findAllEmployee(keyword, pageRequest)
                    .map(UserResponse::fromUser);
            // Lấy tổng số trang
            int totalPages = userPage.getTotalPages();
            List<UserResponse> userResponses = userPage.getContent();
            return ResponseEntity.ok(UserListResponse
                    .builder()
                    .users(userResponses)
                    .totalPages(totalPages)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/auth/social-login")
    public ResponseEntity<String> socialAuth(
            @RequestParam("login_type") String loginType,
            HttpServletRequest request
    ){
        //request.getRequestURI()
        loginType = loginType.trim().toLowerCase();  // Loại bỏ dấu cách và chuyển thành chữ thường
        String url = authService.generateAuthUrl(loginType);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/auth/social/callback")
    public ResponseEntity<ResponseObject> callback(
            @RequestParam("code") String code,
            @RequestParam("login_type") String loginType,
            HttpServletRequest request
    ) {
        try {
            System.out.println("Received code: " + code);
            System.out.println("Login type: " + loginType);
            Map<String, Object> userInfo = authService.authenticateAndFetchProfile(code, loginType);
            System.out.println("User info: " + userInfo);
            if (userInfo == null) {
                return ResponseEntity.badRequest().body(
                        new ResponseObject("Failed to authenticate", HttpStatus.BAD_REQUEST, null));
            }

            String accountId = "";
            String name = "";
            String email = "";

            if (loginType.trim().equals("google")) {
                accountId = String.valueOf(userInfo.get("sub"));
                name = String.valueOf(userInfo.get("name"));
                email = String.valueOf(userInfo.get("email"));
            } else if (loginType.trim().equals("facebook")) {
                accountId = String.valueOf(userInfo.get("id"));
                name = String.valueOf(userInfo.get("name"));
                email = String.valueOf(userInfo.get("email"));
            }

            UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                    .email(email)
                    .fullname(name)
                    .password("")
                    .phoneNumber("")
                    .build();

            if (loginType.trim().equals("google")) {
                userLoginDTO.setGoogleAccountId(accountId);
            } else if (loginType.trim().equals("facebook")) {
                userLoginDTO.setFacebookAccountId(accountId);
            }

            return this.loginSocial(userLoginDTO, request);
        } catch (Exception e) {
            e.printStackTrace(); // In chi tiết stacktrace
            return ResponseEntity.internalServerError().body(
                    new ResponseObject("Internal error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> blockOrEnable(
            @Valid @PathVariable long userId,
            @Valid @PathVariable int active
    ) {
        try {
            userService.blockOrEnable(userId, active > 0);
            String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
            return ResponseEntity.ok().body(message);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<ResponseObject> loginSocial(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        // Gọi hàm loginSocial từ UserService cho đăng nhập mạng xã hội
        String token = userService.loginSocial(userLoginDTO);
        // Xử lý token và thông tin người dùng
        User userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token);

        // Tạo đối tượng LoginResponse
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Đăng nhập thành công")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId())
                .build();

        // Trả về phản hồi
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Login successfully")
                        .data(loginResponse)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

}
