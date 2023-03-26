package just.khao.com.service;

import just.khao.com.entity.AuthEntity;
import just.khao.com.entity.GoogleToken;
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

    public void createProfileFromGoogle(GoogleToken googleToken){
        AuthEntity authEntity = authRepository.findByEmail(googleToken.getEmail());

        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setAuth_id(authEntity.getId());
        profileEntity.setIssuer("GOOGLE");
        profileEntity.setIssuer_user_id(googleToken.getSub());
        profileEntity.setContact_email(googleToken.getEmail());
        profileEntity.setEmail_verified(googleToken.isEmail_verified());
        profileEntity.setFull_name(googleToken.getName());
        profileEntity.setAvatar(googleToken.getPicture());
        profileEntity.setFirst_name(googleToken.getFamily_name());
        profileEntity.setLast_name(googleToken.getGiven_name());

        profileRepository.createProfile(profileEntity);
    }
}
