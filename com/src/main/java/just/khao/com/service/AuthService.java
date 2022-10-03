package just.khao.com.service;

import just.khao.com.entity.AuthEntity;
import just.khao.com.model.SignupModel;
import just.khao.com.repository.postgres.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

//    @Value("${jwt.accesstoken.secretkey}")
//    String accessTokenSecret;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<AuthEntity> findByUsernameOrEmail(String username, String email){
        return authRepository.findByUsernameOrEmail(username, email);
    }

    public void createAuth(SignupModel signupModel){
        signupModel.setHashed_password(passwordEncoder.encode(signupModel.getPassword()));
        authRepository.createAuth(signupModel);
    }

    public Boolean checkPassword(String password, String hashed_password){
        Boolean matched = passwordEncoder.matches(password, hashed_password);
        return matched;
    }
}
