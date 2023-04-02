package just.khao.com.service;

import just.khao.com.entity.AuthorEntity;
import just.khao.com.repository.postgres.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {
    @Autowired
    AuthorRepository authorRepository;

    public void createAuthor(AuthorEntity authorEntity){
        authorRepository.createAuthor(authorEntity);
    }
}
