package com.module2.demo3.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
  @NotBlank(message = "Name is mandatory")
  private String name;

  @NotBlank(message = "Email is mandatory")
  @Email(message = "Email must be valid")
  private String email;
}