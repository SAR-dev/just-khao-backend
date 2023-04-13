package just.khao.com.controller;

import just.khao.com.entity.registerUser.RegisterUserEntity;
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
        userService.registerUser(registerUserEntity);
    }

}
