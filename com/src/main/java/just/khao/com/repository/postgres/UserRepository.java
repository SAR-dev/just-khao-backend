package just.khao.com.repository.postgres;

import just.khao.com.entity.RegisterUserEntity;
import just.khao.com.entity.SigninUserEntity;
import just.khao.com.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {
    public void registerUser(RegisterUserEntity registerUserEntity);

    public UserEntity signInByEmailAndPassword(SigninUserEntity signinUserEntity);

}
