package just.khao.com.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import just.khao.com.entity.AuthEntity;
import just.khao.com.model.*;
import just.khao.com.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public String TestAuth(){
        return "Auth OK";
    }

    @PostMapping("/sign-up")
    public ResponseMessage SignUp(@RequestBody SignupModel signupModel){
        ResponseMessage responseMessage = new ResponseMessage();
        try{
            authService.createAuth(signupModel);
            responseMessage.setStatus(200);
            responseMessage.setMessage("User Created");
        } catch(Exception e){
            responseMessage.setStatus(500);
            responseMessage.setMessage("Follow the guidelines for creating account!");
        }
        return responseMessage;
    }

    @PostMapping("/sign-in")
    public ResponseMessage SignIn(@RequestBody SigninModel signinModel) {
        AuthEntity authEntity = authService.findByUsernameOrEmail(
                signinModel.getUsername(),
                signinModel.getEmail()
        );
        Boolean isAuthenticated = false;
        ResponseMessage responseMessage = new ResponseMessage();
        if(authEntity != null){
            isAuthenticated = authService.verifyPassword(
                    signinModel.getPassword(),
                    authEntity.getHashed_password()
            );
        }
        if(isAuthenticated){
            responseMessage.setData(authService.getNewToken(authEntity));
        } else {
            responseMessage.setStatus(500);
            responseMessage.setMessage("Password doesn't match!");
        }
        return responseMessage;
    }

    @PostMapping("/refresh-token")
    public ResponseMessage RefreshToken(@RequestBody TokenModel tokenModel) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setData(authService.reIssueToken(tokenModel));
        return responseMessage;
    }

    @PostMapping("/google")
    public ResponseMessage GoogleSignIn(@RequestBody GoogleSigninModel googleSigninModel) {
        GoogleIdToken googleIdToken = authService.extractGooleToken(googleSigninModel.getToken());
        ResponseMessage responseMessage = new ResponseMessage();
        if(googleIdToken != null){
            responseMessage.setData(authService.decodeOAuthGooleToken(googleIdToken));
        } else {
            responseMessage.setStatus(500);
            responseMessage.setMessage("Invalid Google Account!");
        }
        return responseMessage;
    }
}
