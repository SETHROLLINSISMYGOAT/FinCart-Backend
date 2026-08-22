package com.fincart.user.service;

import com.fincart.common.exception.DuplicateResourceException;
import com.fincart.common.exception.ResourceNotFoundException;
import com.fincart.user.dto.CreateUserRequest;
import com.fincart.user.dto.UpdateUserRequest;
import com.fincart.user.dto.UserResponse;
import com.fincart.user.entity.User;
import com.fincart.user.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public class UserService {
    private UserRepository userRepository;
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public UserResponse createUser(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUsername(request.getName());
        user.setRole("User");
        User updatedUser =
                userRepository.save(user);

        return toResponse(updatedUser);

    }
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(
                        "User with id " + id + " not found"
                ));
        return toResponse(user);

    }
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(
                request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists"
            );
        }

        user.setUsername(request.getName());
        user.setEmail(request.getEmail());

        User updatedUser =
                userRepository.save(user);

        return toResponse(updatedUser);
    }
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "User with id " + id + " not found"
            );
        }
        userRepository.deleteById(id);
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
