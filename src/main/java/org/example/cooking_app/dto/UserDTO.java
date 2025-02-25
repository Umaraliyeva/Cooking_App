package org.example.cooking_app.dto;

import lombok.Value;

import java.util.List;

@Value
public class UserDTO {
    String email;
    String password;
    String fullName;
    String username;
    List<Integer> roleIds;




}
