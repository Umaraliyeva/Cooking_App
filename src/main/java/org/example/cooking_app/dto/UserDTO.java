package org.example.cooking_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Data
@AllArgsConstructor
public class UserDTO {
    String username;
    String fullName;

}
