package org.sondev.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.sondev.identity.dto.request.ProfileCreationRequest;
import org.sondev.identity.dto.request.UserCreationRequest;
import org.sondev.identity.dto.request.UserUpdateRequest;
import org.sondev.identity.dto.response.UserResponse;
import org.sondev.identity.entity.Role;
import org.sondev.identity.entity.User;
import org.sondev.identity.exception.AppException;
import org.sondev.identity.exception.ErrorCode;
import org.sondev.identity.mapper.ProfileMapper;
import org.sondev.identity.mapper.UserMapper;
import org.sondev.identity.repository.RoleRepository;
import org.sondev.identity.repository.UserRepository;
import org.sondev.identity.repository.httpclient.ProfileClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ProfileClient profileClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * @General: Tạo mới 1 user
     */
    public UserResponse createUser(UserCreationRequest request) {
        log.info("Service: createUser");

        // Tạo người dùng từ request.
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Lấy vai trò 'USER' hoặc tạo mới nếu chưa có.
        Role userRole = roleRepository.findById("USER").orElseGet(() -> {
            // Tạo mới vai trò nếu không tồn tại.
            Role role = Role.builder().name("USER").description("User role").build();
            return roleRepository.save(role);
        });
        // Gắn vai trò vào người dùng.
        user.setRoles(Set.of(userRole));
        // Lưu người dùng và trả về phản hồi.
        try {
            // Tạo user ở identity-service
            user = userRepository.save(user);

            // Tạo profile ở profile-service
            ProfileCreationRequest profileCreationRequest = profileMapper.toProfileCreationRequest(request);
            profileCreationRequest.setUserId(user.getId());

            var profileResponse = profileClient.createProfile(profileCreationRequest);
            log.info("profile Response: {}", profileResponse.toString());

        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }

        // public message to kafka
        kafkaTemplate.send("onboard-successful", "Welcome our new member: " + user.getUsername());

        return userMapper.toUserResponse(user);
    }

    /**
     * @General
     * @Description: Phân quyền trên method. Trước khi vào method, kiểm tra xem user có ROLE_ADMIN
     * Cần có @EnableMethodSecurity ở class cấu hình dự án (@Configuration)
     * Ngoài ra còn có thể sử dụng PreAuthorize("hasAuthoriity('ROLE_ADMIN')")
     * @Note: khi dùng {@code @PreAuthorize("hasRole('ADMIN')")} sẽ map với user có role = "ROLE_ADMIN".
     * Khi dùng {@code @PreAuthorize("hasAuthority('APPROVE_POST')")} set map chính xác với user có role = "APPROVE_POST"
     */
    @PreAuthorize(value = "hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method getUsers");
        return userRepository
                .findAll() // trả về một danh sách các User.
                .stream() // tạo một luồng (stream) từ danh sách.
                .map(user ->
                        userMapper.toUserResponse(user)) // chuyển đổi mỗi đối tượng User thành đối tượng UserResponse.
                .collect(Collectors.toList()); // chuyển kết quả từ Stream<UserResponse> về List<UserResponse>.
    }

    /**
     * @General: Lấy thông tin User
     * @Description: User cần cung cấp userId
     * @param: {@code String} userId
     * @more: Config để chỉ user mới có thể get thông tin của chính mình
     */
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id) {
        log.info("In method getUserById");
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    /**
     * @General: Lấy thông tin User.
     * @Description: Dựa vào token trong request, lấy ra thông tin User và trả về cho Client.
     * @More: Client không cần phải gửi thêm bất kỳ thông tin nào.
     */
    public UserResponse getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();
        User user =
                userRepository.findByUsername(userName).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        return userMapper.toUserResponse(user);
    }

    /**
     * @General: Cập nhật thông tin User
     * @Description:
     * @Detail:
     */
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        List<Role> roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    /**
     * @General
     */
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
