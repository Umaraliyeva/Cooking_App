package org.example.cooking_app.dto;

import lombok.Data;
import lombok.Value;

@Value
public class LoginDTO {
    String username;
    String password;
}
