package just.khao.com.controller;

import just.khao.com.entity.AuthEntity;
import just.khao.com.model.SigninModel;
import just.khao.com.model.SignupModel;
import just.khao.com.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public ResponseEntity<String> SignUp(@RequestBody SignupModel signupModel){
        try{
            authService.createAuth(signupModel);
            return ResponseEntity.ok().body("User Created");
        } catch(Exception e){
            return ResponseEntity.badRequest().body("Follow the guidelines for creating account!");
        }
    }

    @PostMapping("/sign-in")
    public Boolean SignIn(@RequestBody SigninModel signinModel){
        Optional<AuthEntity> authEntity = authService.findByUsernameOrEmail(signinModel.getUsername(), signinModel.getEmail());
        Boolean isAuthenticated;
        if(!authEntity.isEmpty()){
            isAuthenticated = authService.checkPassword(signinModel.getPassword(), authEntity.get().getHashed_password());
        } else {
            isAuthenticated = false;
        }
        return isAuthenticated;
    }
}
