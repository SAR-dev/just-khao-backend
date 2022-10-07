package just.khao.com.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import just.khao.com.entity.AuthEntity;
import just.khao.com.entity.ProfileEntity;
import just.khao.com.repository.postgres.AuthRepository;
import just.khao.com.repository.postgres.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final AuthRepository authRepository;

    public ProfileService(ProfileRepository profileRepository, AuthRepository authRepository) {
        this.profileRepository = profileRepository;
        this.authRepository = authRepository;
    }

    public ProfileEntity findByEmail(String email){
        return profileRepository.findByEmail(email);
    }

    public ProfileEntity findByUsername(String username){
        return profileRepository.findByUsername(username);
    }

    public void createProfileFromGoogle(GoogleIdToken googleIdToken){
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        AuthEntity authEntity = authRepository.findByEmail(payload.getEmail());

        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setAuth_id(authEntity.getId());
        profileEntity.setIssuer("GOOGLE");
        profileEntity.setIssuer_user_id(payload.getSubject());
        profileEntity.setContact_email(payload.getEmail());
        profileEntity.setEmail_verified(Boolean.valueOf(payload.getEmailVerified()));
        profileEntity.setFull_name((String) payload.get("name"));
        profileEntity.setAvatar((String) payload.get("picture"));
        profileEntity.setFirst_name((String) payload.get("family_name"));
        profileEntity.setLast_name((String) payload.get("given_name"));

        profileRepository.createProfile(profileEntity);
    }
}
