package just.khao.com.repository.postgres;

import just.khao.com.entity.AuthEntity;
import just.khao.com.model.IssueTokenModel;
import just.khao.com.model.SignupModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRepository {
    AuthEntity findByUsernameOrEmail(String username, String email);

    void createAuth(SignupModel signupModel);

    void createGoogleAuth(SignupModel signupModel);

    Boolean updateToken(IssueTokenModel issueTokenModel);
}
