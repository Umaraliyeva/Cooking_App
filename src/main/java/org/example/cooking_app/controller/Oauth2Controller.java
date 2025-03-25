package org.example.cooking_app.controller;

import jakarta.servlet.annotation.MultipartConfig;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.dto.OauthClientDTO;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.UserRepository;
import org.example.cooking_app.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/oauth")
@RequiredArgsConstructor
@MultipartConfig
public class Oauth2Controller {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    @Value("${spring.oauth.auth-url}")
    private  String authUrl;
    @Value("${spring.oauth.client-id}")
    private String clientId;
    @Value("${spring.oauth.client-secret}")
    private String clientSecret;
    @Value("${spring.oauth.redirect-url}")
    private String redirectUri;
    @Value("${spring.oauth.token-url}")
    private String tokenUrl;
    @Value("${spring.oauth.USER-INFO-URL}")
    private String USER_INFO_URL;




    @GetMapping("/oauth2")
    public ResponseEntity<?> oauth2() {
        String authorizationUrl = authUrl + "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code&scope=openid%20profile%20email";
        Map<String,String> response=new HashMap<>();
        response.put("authorizationUrl",authorizationUrl);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/oauth2/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        System.out.println("Received code from Google: " + code);

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id",clientId);
        params.add("client_secret",clientSecret);
        params.add("code",code);
        params.add("grant_type","authorization_code");
        params.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, requestEntity, Map.class);
        Map<String,String> tokens=response.getBody();

        String accessToken=tokens.get("access_token");
        String userName=fetchUserInfo(accessToken);

        System.out.println("User Email: " + userName);

        User user = userRepository.findByUsername(userName).orElseThrow();
        String jwtToken=tokenService.generateToken(user);
        System.out.println(jwtToken);

        Map<String,String> responseBody= new HashMap<>();
        responseBody.put("access_token",jwtToken);
        return ResponseEntity.ok(responseBody);

    }

    private String fetchUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<OauthClientDTO> response=restTemplate.exchange(
                USER_INFO_URL,
                HttpMethod.GET,
                entity,
                OauthClientDTO.class
        );

        OauthClientDTO userInfo=response.getBody();
        System.out.println(userInfo);
        assert userInfo != null;
        return  userInfo.getEmail();

    }


}
