package just.khao.com.controller;

import just.khao.com.entity.AuthorEntity;
import just.khao.com.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/author")
public class AuthorController {
    @Autowired
    AuthorService authorService;

    @PostMapping("/create")
    public void createAuthor(@RequestBody AuthorEntity authorEntity) throws Exception {
        try {
            authorService.createAuthor(authorEntity);
        } catch (Exception e){
            throw new Exception("Your author account could not be initialized");
        }
    }
}
