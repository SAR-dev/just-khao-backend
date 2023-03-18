package just.khao.com.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/test")
    public String TestAuth(){
        return "Auth OK";
    }
}
