package just.khao.com.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import just.khao.com.entity.AuthEntity;
import just.khao.com.entity.ProfileEntity;
import just.khao.com.model.IssueTokenModel;
import just.khao.com.model.SignupModel;
import just.khao.com.model.TokenModel;
import just.khao.com.repository.postgres.AuthRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@Service
@Transactional
public class AuthService {
    @Value("${jwt.accesstoken.expirationtime}")
    long accessTokenExpTime;

    @Value("${jwt.refreshtoken.expirationtime}")
    long refreshTokenExpTime;

    @Value("${jwt.accesstoken.secretkey}")
    String accessTokenSecret;

    @Value("${google.client_id}")
    String client_id;

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthEntity findByUsernameOrEmail(String username, String email){
        return authRepository.findByUsernameOrEmail(username, email);
    }

    public void createAuth(SignupModel signupModel){
        signupModel.setHashed_password(passwordEncoder.encode(signupModel.getPassword()));
        authRepository.createAuth(signupModel);
    }

    public void createGoogleAuth(SignupModel signupModel){
        authRepository.createGoogleAuth(signupModel);
    }

    public Boolean verifyPassword(String password, String hashed_password){
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

    public TokenModel getNewToken(AuthEntity authEntity){
        IssueTokenModel issueTokenModel = new IssueTokenModel();
        TokenModel tokenModel = new TokenModel();

        issueTokenModel.setId(authEntity.getId());
        issueTokenModel.setRefresh_token(getRefreshToken());
        issueTokenModel.setRefreshed_at(getRefreshedAt());

        authRepository.updateToken(issueTokenModel);

        tokenModel.setAccess_token(getAccessToken(authEntity.getEmail()));
        tokenModel.setRefresh_token(passwordEncoder.encode(issueTokenModel.getRefresh_token()));
        return  tokenModel;
    }

    public boolean verifyRefreshToken(TokenModel tokenModel, AuthEntity authEntity){
        Boolean hasOldToken = (authEntity.getRefreshed_at() != null && StringUtils.isNotEmpty(authEntity.getRefresh_token()));
        Boolean hasValidity = authEntity.getRefreshed_at().plus(refreshTokenExpTime, ChronoUnit.SECONDS).isAfter(Instant.now());
        Boolean isValid = passwordEncoder.matches(authEntity.getRefresh_token(), tokenModel.getRefresh_token());
        return (hasOldToken && hasValidity && isValid);
    }

    public TokenModel reIssueToken(TokenModel tokenModel){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(accessTokenSecret)).build();
        String email = verifier.verify(tokenModel.getAccess_token()).getSubject();
        AuthEntity authEntity = findByUsernameOrEmail("", email);

        TokenModel newToken = new TokenModel();
        if(verifyRefreshToken(tokenModel ,authEntity)){
            newToken = getNewToken(authEntity);
        };
        return newToken;
    }

    public GoogleIdToken extractGooleToken(String token){
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(client_id))
                .build();
        GoogleIdToken googleIdToken;
        try {
            googleIdToken = verifier.verify(token);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return googleIdToken;
    }

    @Transactional
    public TokenModel decodeOAuthGooleToken(GoogleIdToken googleIdToken){
        ProfileEntity profileEntity = new ProfileEntity();
        SignupModel signupModel = new SignupModel();

        Payload payload = googleIdToken.getPayload();

        profileEntity.setIssuer("GOOGLE");
        profileEntity.setIssuer_user_id(payload.getSubject());
        profileEntity.setContact_email(payload.getEmail());
        profileEntity.setEmail_verified(Boolean.valueOf(payload.getEmailVerified()));
        profileEntity.setFull_name((String) payload.get("name"));
        profileEntity.setAvatar((String) payload.get("picture"));
        profileEntity.setFirst_name((String) payload.get("family_name"));
        profileEntity.setLast_name((String) payload.get("given_name"));

        signupModel.setEmail(payload.getEmail());
        signupModel.setUsername(profileEntity.getFirst_name() + '.' + profileEntity.getLast_name());

        createGoogleAuth(signupModel);
//        Then create profile
//        System.out.println(profileEntity);

        AuthEntity authEntity = findByUsernameOrEmail("", signupModel.getEmail());
        TokenModel tokenModel = getNewToken(authEntity);
        return tokenModel;
    }
}
