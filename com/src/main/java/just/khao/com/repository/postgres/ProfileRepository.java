package just.khao.com.repository.postgres;

import just.khao.com.entity.ProfileEntity;
import just.khao.com.model.SignupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfileRepository {
    void createProfile(ProfileEntity profileEntity);

    ProfileEntity findByEmail(String email);

    ProfileEntity findByUsername(String username);
}
