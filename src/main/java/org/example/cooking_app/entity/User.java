package org.example.cooking_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    @Email(message = "email xato qaytadan urinib ko'ring ")
    private String username;
    private String password;
    private String fullName;
    @ManyToOne(fetch = FetchType.EAGER)
    private Attachment profilePicture;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
    private Integer tempCode;
    private String info;
    @ManyToMany(fetch = FetchType.EAGER)
    List<User> followers;
    @ManyToMany(fetch = FetchType.EAGER)
    List<User> followings;
    private String profession;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Recipe> savedRecipes;




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }
}
