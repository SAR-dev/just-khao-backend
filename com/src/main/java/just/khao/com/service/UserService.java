package just.khao.com.service;

import just.khao.com.entity.RegisterUserEntity;
import just.khao.com.repository.postgres.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void registerUser(RegisterUserEntity registerUserEntity){
        userRepository.registerUser(registerUserEntity);
    }

}
