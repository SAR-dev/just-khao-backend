package just.khao.com.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import just.khao.com.entity.AuthEntity;
import just.khao.com.model.*;
import just.khao.com.service.AuthService;
import just.khao.com.service.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final ProfileService profileService;
    public AuthController(AuthService authService, ProfileService profileService) {
        this.authService = authService;
        this.profileService = profileService;
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
            responseMessage.setMessage("User Created");
        } catch(Exception e){
            responseMessage.setStatus(500);
            responseMessage.setMessage("Follow the guidelines for creating account!");
        }
        return responseMessage;
    }

    @PostMapping("/sign-in")
    public ResponseMessage SignIn(@RequestBody SigninModel signinModel) {
        ResponseMessage responseMessage = new ResponseMessage();
        AuthEntity authEntity = authService.findByUsernameOrEmail(
                signinModel.getUsername(),
                signinModel.getEmail()
        );
        Boolean isAuthenticated = false;
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
            TokenModel tokenModel = authService.createTokenFromGoogle(googleIdToken);
            profileService.createProfileFromGoogle(googleIdToken);
            responseMessage.setData(tokenModel);
        } else {
            responseMessage.setStatus(500);
            responseMessage.setMessage("Invalid Google Account!");
        }
        return responseMessage;
    }
}
