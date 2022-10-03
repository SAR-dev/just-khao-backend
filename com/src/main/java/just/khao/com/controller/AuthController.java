package just.khao.com.controller;

import just.khao.com.entity.AuthEntity;
import just.khao.com.model.ResponseMessage;
import just.khao.com.model.SigninModel;
import just.khao.com.model.SignupModel;
import just.khao.com.model.TokenModel;
import just.khao.com.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
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
    public ResponseMessage SignUp(@RequestBody SignupModel signupModel){
        ResponseMessage responseMessage = new ResponseMessage();
        try{
            authService.createAuth(signupModel);
            responseMessage.setStatus(200);
            responseMessage.setMessage("User Created");
        } catch(Exception e){
            responseMessage.setStatus(403);
            responseMessage.setMessage("Follow the guidelines for creating account!");
        }
        return responseMessage;
    }

    @PostMapping("/sign-in")
    public TokenModel SignIn(@RequestBody SigninModel signinModel) throws AuthenticationException {
        Optional<AuthEntity> authEntity = authService.findByUsernameOrEmail(signinModel.getUsername(), signinModel.getEmail());
        Boolean isAuthenticated;
        if(!authEntity.isEmpty()){
            isAuthenticated = authService.checkPassword(signinModel.getPassword(), authEntity.get().getHashed_password());
        } else {
            isAuthenticated = false;
        }
        if(isAuthenticated){
            return authService.updateToken(authEntity);
        } else {
            throw new AuthenticationException("Password doesn't match!");
        }
    }
}
