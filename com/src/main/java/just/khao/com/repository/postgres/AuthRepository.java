package just.khao.com.repository.postgres;

import just.khao.com.entity.AuthEntity;
import just.khao.com.model.SignupModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface AuthRepository {
    Optional<AuthEntity> findByUsernameOrEmail(String username, String email);

    void createAuth(SignupModel signupModel);
}
