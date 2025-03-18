package org.example.cooking_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.dto.LoginDTO;
import org.example.cooking_app.entity.Attachment;
import org.example.cooking_app.entity.AttachmentContent;
import org.example.cooking_app.entity.Role;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.enums.RoleName;
import org.example.cooking_app.repo.AttachmentContentRepository;
import org.example.cooking_app.repo.AttachmentRepository;
import org.example.cooking_app.repo.RoleRepository;
import org.example.cooking_app.repo.UserRepository;
import org.example.cooking_app.service.EmailService;
import org.example.cooking_app.service.EmailVerificationService;
import org.example.cooking_app.service.TokenService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
//@MultipartConfig

public class AuthController {



    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;
    private final AttachmentContentRepository attachmentContentRepository;
    private final RoleRepository roleRepository;
    private final AttachmentRepository attachmentRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);

            User user = (User) authenticate.getPrincipal();
            String token = tokenService.generateToken(user);

            return ResponseEntity.ok(Map.of("token", token)); // JSON shaklida qaytarish
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<?> registerUser(
            @RequestParam("username") String username,
            @RequestParam("fullName") String fullName,
            @RequestParam("password") String password,
            @RequestParam(value = "file",required = false) MultipartFile file) throws IOException {

        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .build();
        attachmentRepository.save(attachment);

        AttachmentContent attachmentContent = AttachmentContent.builder()
                .content(file.getBytes())
                .attachment(attachment)
                .build();
        attachmentContentRepository.save(attachmentContent);

        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow();

    User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .profilePicture(attachment)
                .roles(new ArrayList<>(List.of(userRole)))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @Tag(name="forgot-password  button ni bossa ishlaydi",description = " undan keyin verify code page o'tadi email to'g'ri kiritsa  ")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {
        System.out.println("Forgot password request for: " + email);

        if (emailVerificationService.isBlocked(email)) {
            System.out.println("User is blocked: " + email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Siz 3 martadan ortiq xato urindingiz. Hisobingiz vaqtincha bloklandi. Iltimos, ertaga urinib ko'ring!");
        }

        Optional<User> userOpt = userRepository.findByUsername(email);
        if (userOpt.isEmpty()) {
            System.out.println("User not found: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found!");
        }else {

            String code = emailService.sendVerificationCode(email);
            userOpt.get().setTempCode(Integer.parseInt(code));
            userRepository.save(userOpt.get());
            emailVerificationService.saveCode(email, code);

            System.out.println("Verification code sent to: " + email);
            return ResponseEntity.status(201).body("Tasdiqlash kodingiz emailga yuborildi!");
        }
    }
    @Tag(name="6 xonalik emailga kelgan code ni tekshiradi",description = " agar to'g'ri bo'lsa reset-password page ga o'tadi ")
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        System.out.println(code);
        if (emailVerificationService.verifyCode(email, code)) {
            return ResponseEntity.ok("Kodni tasdiqlash muvaffaqiyatli! Yangi parolni kiriting.");
        } else {
            if (emailVerificationService.isBlocked(email)) {
                return ResponseEntity.status(403).body("3 martadan ortiq xato kod kiritildi. Hisobingiz vaqtincha bloklandi.");
            }
            return ResponseEntity.status(400).body("Kod noto‘g‘ri. Qayta urinib ko‘ring!");
        }
    }
    @Tag(name="yangi password kiritadi")
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(email);
        if (userOpt.isEmpty()) {
            return "Email noto‘g‘ri!";
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Parol muvaffaqiyatli o‘zgartirildi!";
    }



}
