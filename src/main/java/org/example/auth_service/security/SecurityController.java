package org.example.auth_service.security;


import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.security.config.auth_entity.AuthEntityService;
import org.example.auth_service.security.dto.SignUpForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final AuthEntityService authEntityService;
    private final JwtUtils jwtUtils;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpForm signUpForm) {
        String accessToken = authEntityService.saveAuthEntity(signUpForm);
        ApiResponse<String> apiResponse = new ApiResponse<>("sign up succeed", HttpStatus.OK, accessToken);
        return new ResponseEntity<>(new Gson().toJson(apiResponse), HttpStatus.OK);
    }
}
