package com.project.shop_dior.service;

import com.project.shop_dior.component.JwtTokenUtil;
import com.project.shop_dior.component.LocalizationUtils;
import com.project.shop_dior.dtos.UpdateUserDTO;
import com.project.shop_dior.dtos.UserDTO;
import com.project.shop_dior.dtos.UserLoginDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.exception.PermissionDenyException;
import com.project.shop_dior.models.Role;
import com.project.shop_dior.models.Token;
import com.project.shop_dior.models.User;
import com.project.shop_dior.repository.RoleRepository;
import com.project.shop_dior.repository.TokenRepository;
import com.project.shop_dior.repository.UserRepository;
import com.project.shop_dior.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

private final UserRepository userRepository;
private  final RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;
private final JwtTokenUtil jwtTokenUtil;
private final AuthenticationManager authenticationManager;
private final LocalizationUtils localizationUtils;
private  final TokenRepository tokenRepository;



    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();

        // Kiểm tra số điện thoại đã tồn tại
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataNotFoundException("SĐT đã tồn tại");
        }

        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        if (role.getName().equals(Role.ADMIN)) {
            throw new PermissionDenyException("You cannot register an admin account!");
        }

        // Nếu không phải tài khoản social, bắt buộc mã hóa password
        String encodedPassword = "";
        if (userDTO.getFacebookAccountId() == null && userDTO.getGoogleAccountId() == null) {
            encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        }

        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(encodedPassword) //
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .role(role)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }
        User existingUser = optionalUser.get();
        //check passwor
        if (existingUser.getFacebookAccountId() == null && existingUser.getGoogleAccountId() == null) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
            }
        }
        if (!existingUser.isActive()) {
            throw new DataNotFoundException("Tài khoản đã bị khóa");
        }
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isEmpty()||!roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password , existingUser.getAuthorities()
        );
        //authenticate with  Jva Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generationToken(optionalUser.get());
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)){ //Sử dụng tiện ích jwtTokenUtil để kiểm tra xem token có hết hạn không
            throw new Exception("Token is expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);//trich xuat sdt tu token
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);//tim kiem nguoi dung theo sdt
        if (user.isPresent()){
            return user.get();
        }else{
            throw new Exception("User not found");
        }
    }

    @Override
    public User getUserById(long id) {
return userRepository.findById(id).orElseThrow(()
->new RuntimeException("Employee not found"));
    }
    @Override
    public User updateEmployee(long userId, UserDTO userDTO) {
        User existingUser = getUserById(userId);
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setFullName(userDTO.getFullName());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setDateOfBirth(userDTO.getDateOfBirth());
        return userRepository.save(existingUser);
    }


    @Override
    @Transactional
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws DataNotFoundException {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            String newPhoneNumber = updateUserDTO.getPhoneNumber();

            if (updateUserDTO.getCurrentPassword() == null
                    || !passwordEncoder.matches(updateUserDTO.getCurrentPassword(), existingUser.getPassword())) {
                throw new DataNotFoundException("Mật khẩu hiện tại không đúng");
            }

            if (newPhoneNumber != null &&
                    !newPhoneNumber.equals(existingUser.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(newPhoneNumber)) {
                throw new DataIntegrityViolationException("Phone number đã tồn tại");
            }

            if (newPhoneNumber != null) {
                existingUser.setPhoneNumber(newPhoneNumber);
            }

            if (updateUserDTO.getFullName() != null) {
                existingUser.setFullName(updateUserDTO.getFullName());
            }

            if (updateUserDTO.getAddress() != null) {
                existingUser.setAddress(updateUserDTO.getAddress());
            }

            if (updateUserDTO.getDateOfBirth() != null) {
                existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
            }

            if (updateUserDTO.getFacebookAccountId() != null) {
                existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
            }

            if (updateUserDTO.getGoogleAccountId() != null) {
                existingUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
            }

            if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
                if (!updateUserDTO.getPassword().equals(updateUserDTO.getRetypePassword())) {
                    throw new DataNotFoundException("Password and retype password not the same");
                }
                String encodedPassword = passwordEncoder.encode(updateUserDTO.getPassword());
                existingUser.setPassword(encodedPassword);
            }

            User updatedUser = userRepository.save(existingUser);
            System.out.println(" Update thành công cho user ID: " + userId);
            return updatedUser;

        } catch (Exception e) {
            System.err.println(" Lỗi khi updateUser: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String loginSocial(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        Role roleUser = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));

        // Kiểm tra Google Account ID
        if (userLoginDTO.isGoogleAccountIdValid()) {
            optionalUser = userRepository.findByGoogleAccountId(userLoginDTO.getGoogleAccountId());

            // Tạo người dùng mới nếu không tìm thấy
            if (optionalUser.isEmpty()) {
                User newUser = User.builder()
                        .fullName(Optional.ofNullable(userLoginDTO.getFullname()).orElse(""))
                        .email(Optional.ofNullable(userLoginDTO.getEmail()).orElse(""))
                        .phoneNumber(generateRandomPhoneNumber())
                        .role(roleUser)
                        .facebookAccountId(userLoginDTO.getFacebookAccountId())
                        .password("") // Mật khẩu trống cho đăng nhập mạng xã hội
                        .googleAccountId(userLoginDTO.getGoogleAccountId())
                        .active(true)
                        .build();

                // Lưu người dùng mới
                newUser = userRepository.save(newUser);
                optionalUser = Optional.of(newUser);
            }
        }
        // Kiểm tra Facebook Account ID
        else if (userLoginDTO.isFacebookAccountIdValid()) {
            optionalUser = userRepository.findByFacebookAccountId(userLoginDTO.getFacebookAccountId());


            if (optionalUser.isEmpty()) {
                Optional<User> userWithSameEmail = userRepository.findByEmail(userLoginDTO.getEmail());
                if (userWithSameEmail.isPresent()) {
                    // Nếu đã có user dùng email này → cập nhật accountId nếu cần
                    User existing = userWithSameEmail.get();

                    if (userLoginDTO.isGoogleAccountIdValid() && existing.getGoogleAccountId() == null) {
                        existing.setGoogleAccountId(userLoginDTO.getGoogleAccountId());
                    }
                    if (userLoginDTO.isFacebookAccountIdValid() && existing.getFacebookAccountId() == null) {
                        existing.setFacebookAccountId(userLoginDTO.getFacebookAccountId());
                    }

                    existing = userRepository.save(existing);
                    optionalUser = Optional.of(existing);
                } else {
                    // Tạo user mới
                    User newUser = User.builder()
                            .fullName(Optional.ofNullable(userLoginDTO.getFullname()).orElse(""))
                            .email(Optional.ofNullable(userLoginDTO.getEmail()).orElse(""))
                            .role(roleUser)
                            .facebookAccountId(userLoginDTO.getFacebookAccountId())
                            .googleAccountId(userLoginDTO.getGoogleAccountId())
                            .password("")
                            .active(true)
                            .build();

                    newUser = userRepository.save(newUser);
                    optionalUser = Optional.of(newUser);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid social account information.");
        }

        User user = optionalUser.get();

        // Thêm đoạn này để đảm bảo role luôn là 'user'
        if (user.getRole() == null || !user.getRole().getName().equals("user")||!user.getRole().getName().equals("admin")) {
            user.setRole(roleUser);
            user = userRepository.save(user);
        }

        // Kiểm tra nếu tài khoản bị khóa
        if (!user.isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        // Tạo JWT token cho người dùng
        return jwtTokenUtil.generationToken(user);
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        return userRepository.findAll(keyword,pageable);
    }
    @Override
    public Page<User> findAllEmployee(String keyword, Pageable pageable) throws Exception {
        return userRepository.findAllEmployee(keyword,pageable);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);
        userRepository.save(existingUser);
        //reset password => clear token
        List<Token> tokens = tokenRepository.findByUser(existingUser);
        for (Token token : tokens) {
            tokenRepository.delete(token);
        }
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }
    private String generateRandomPhoneNumber() {
        String digits = UUID.randomUUID().toString().replaceAll("\\D", "");
        if (digits.length() < 10) {
            digits += "0123456789";
        }
        return digits.substring(0, 10);
    }
}
