package just.khao.com.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import just.khao.com.entity.AuthEntity;
import just.khao.com.model.IssueTokenModel;
import just.khao.com.model.SignupModel;
import just.khao.com.model.TokenModel;
import just.khao.com.repository.postgres.AuthRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    @Value("${jwt.accesstoken.expirationtime}")
    long accessTokenExpTime;

    @Value("${jwt.refreshtoken.expirationtime}")
    long refreshTokenExpTime;

    @Value("${jwt.accesstoken.secretkey}")
    String accessTokenSecret;

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

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

    public String getAccessToken(String email){
        Instant now = Instant.now();
        String access_token = JWT.create()
                .withSubject(email)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(accessTokenExpTime, ChronoUnit.SECONDS)))
                .sign(Algorithm.HMAC256(accessTokenSecret));
        return access_token;
    }

    public String getRefreshToken() {
        final String token = RandomStringUtils.randomAlphanumeric(25);
        return passwordEncoder.encode(token);
    }

    public Timestamp getRefreshedAt() {
        return Timestamp.from(Instant.now());
    }

    public TokenModel updateToken(Optional<AuthEntity> authEntity){
        IssueTokenModel issueTokenModel = new IssueTokenModel();
        TokenModel tokenModel = new TokenModel();

        issueTokenModel.setId(authEntity.get().getId());
        issueTokenModel.setRefresh_token(getRefreshToken());
        issueTokenModel.setRefreshed_at(getRefreshedAt());

        authRepository.updateToken(issueTokenModel);

        tokenModel.setAccess_token(getAccessToken(authEntity.get().getEmail()));
        tokenModel.setRefresh_token(issueTokenModel.getRefresh_token());
        return  tokenModel;
    }
}
