package just.khao.com.controller;

import just.khao.com.entity.Author.RegisterAuthor.RegisterAuthorRequest;
import just.khao.com.service.AuthorService;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/author")
public class AuthorController {
    @Autowired
    AuthorService authorService;

    @GetMapping("/test")
    public String TestUser(){
        return "Author OK";
    }

    @PostMapping("/register")
    public void registerAuthor(@RequestBody RegisterAuthorRequest request) throws NotFoundException {
        authorService.registerAuthor(request);
    }
}
