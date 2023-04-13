package just.khao.com.service;

import just.khao.com.entity.User.UserEntity;
import just.khao.com.entity.registerUser.RegisterUserEntity;
import just.khao.com.entity.registerUser.RegisterUserRequest;
import just.khao.com.repository.postgres.UserRepository;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void registerUser(RegisterUserEntity registerUserEntity){
        userRepository.registerUser(registerUserEntity);
    }

    public UserEntity getUserByName(String username) throws NotFoundException {
        UserEntity user = userRepository.getUserByName(username);
        if (user == null) {
            String message = "user is not found";
            throw new NotFoundException(message);
        }
        return user;
    }

}
