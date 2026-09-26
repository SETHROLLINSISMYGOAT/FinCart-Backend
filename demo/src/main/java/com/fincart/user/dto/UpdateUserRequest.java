package com.fincart.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateUserRequest {
    @NotBlank(message = "Name is required")
    private String name;





    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }
}
