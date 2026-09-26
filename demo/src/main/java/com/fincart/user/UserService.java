package com.fincart.user.service;

import com.fincart.common.exception.ResourceNotFoundException;
import com.fincart.user.dto.UpdateUserRequest;
import com.fincart.user.dto.UserResponse;
import com.fincart.user.entity.User;
import com.fincart.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return toResponse(user);
    }

    @Transactional
    public UserResponse updateMe(
            String authenticatedEmail,
            UpdateUserRequest request
    ) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        user.setUsername(request.getName().trim());


        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}