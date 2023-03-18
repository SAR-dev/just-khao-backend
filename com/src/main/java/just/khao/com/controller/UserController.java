package just.khao.com.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test")
    public String TestUser(){
        return "Auth OK";
    }
}
