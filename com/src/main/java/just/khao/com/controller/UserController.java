package just.khao.com.controller;

import just.khao.com.entity.RegisterUserEntity;
import just.khao.com.entity.SigninUserEntity;
import just.khao.com.entity.UserEntity;
import just.khao.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String TestUser(){
        return "Auth OK";
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody RegisterUserEntity registerUserEntity){
        try {
            userService.registerUser(registerUserEntity);
        } catch(Exception e) {
           throw new RuntimeException("Please try with changing your info");
        }
    }

    @PostMapping("/sign-in")
    public UserEntity signIn(@RequestBody SigninUserEntity signinUserEntity) throws Exception {
        UserEntity userEntity = userService.signInByEmailAndPassword(signinUserEntity);
        if(userEntity == null){
            throw new Exception("Invalid email or password");
        } else {
            return userEntity;
        }


//        1. Check if user exists in database with given email and password
//        2. If exists then return the user object without password
//        3. If doesn't exist the return error
    }

}
