package just.khao.com.service;

import just.khao.com.entity.Author.AuthorEntity;
import just.khao.com.entity.Author.RegisterAuthor.RegisterAuthorRequest;
import just.khao.com.entity.User.UserEntity;
import just.khao.com.repository.postgres.AuthorRepository;
import just.khao.com.repository.postgres.UserRepository;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    UserRepository userRepository;

    public void registerAuthor(RegisterAuthorRequest request) throws NotFoundException {
        UserEntity user = userRepository.getUserByName(request.username);

        if (user == null) {
            String message = "user is not found";
            throw new NotFoundException(message);
        }

        AuthorEntity author = new AuthorEntity();
        author.user_id = user.user_id;
        author.writer_name = request.writer_name;
        author.intro = request.intro;
        author.about_me = request.about_me;

        authorRepository.registerAuthor(author);
    }
}
