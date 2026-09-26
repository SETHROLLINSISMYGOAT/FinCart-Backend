package com.fincart.user.controller;

import com.fincart.user.dto.UpdateUserRequest;
import com.fincart.user.dto.UserResponse;
import com.fincart.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return userService.getMe(authentication.getName());
    }

    @PutMapping("/me")
    public UserResponse updateMe(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return userService.updateMe(authentication.getName(), request);
    }
}