package just.khao.com.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import just.khao.com.entity.AuthEntity;
import just.khao.com.entity.GoogleToken;
import just.khao.com.entity.ProfileEntity;
import just.khao.com.helpers.Converter;
import just.khao.com.model.IssueTokenModel;
import just.khao.com.model.SignupModel;
import just.khao.com.model.TokenModel;
import just.khao.com.repository.postgres.AuthRepository;
import just.khao.com.repository.postgres.ProfileRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProfileRepository profileRepository;

    private final Converter converter;

    public AuthService(AuthRepository authRepository, ProfileRepository profileRepository, @Lazy PasswordEncoder passwordEncoder, ProfileRepository profileRepository1, Converter converter) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository1;
        this.converter = converter;
    }

    public AuthEntity findByUsernameOrEmail(String username, String email){
        return authRepository.findByUsernameOrEmail(username, email);
    }

    public AuthEntity findByEmail(String email){
        return authRepository.findByEmail(email);
    }

    public void createAuth(SignupModel signupModel){
        signupModel.setHashed_password(passwordEncoder.encode(signupModel.getPassword()));
        authRepository.createAuth(signupModel);
        AuthEntity authEntity = authRepository.findByEmail(signupModel.getEmail());

        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setAuth_id(authEntity.getId());
        profileEntity.setIssuer("NATIVE");
        profileEntity.setContact_email(authEntity.getEmail());

        profileRepository.createProfile(profileEntity);
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
        AuthEntity authEntity = findByEmail(email);

        TokenModel newToken = new TokenModel();
        if(verifyRefreshToken(tokenModel ,authEntity)){
            newToken = getNewToken(authEntity);
        };
        return newToken;
    }

    public GoogleToken extractGooleToken(String token) throws IOException, InterruptedException {
        String accessToken = token;
        String endpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        String authorizationHeader = "Bearer " + accessToken;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", authorizationHeader)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        GoogleToken googleToken = new Gson().fromJson(responseBody, GoogleToken.class);
        return googleToken;
    }

    public void createAuthFromGoogle(GoogleToken googleToken){
        SignupModel signupModel = new SignupModel();
        signupModel.setEmail(googleToken.getEmail());
        signupModel.setUsername(converter.convertToAlphanumericWithUnderscore((googleToken.getFamily_name() + '.' + googleToken.getGiven_name())));
        createGoogleAuth(signupModel);
    }

    public TokenModel createTokenFromGoogle(GoogleToken googleToken){
        createAuthFromGoogle(googleToken);
        AuthEntity authEntity = findByEmail(googleToken.getEmail());
        TokenModel tokenModel = getNewToken(authEntity);
        return tokenModel;
    }
}
