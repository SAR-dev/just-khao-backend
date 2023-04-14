package just.khao.com.repository.postgres;

import just.khao.com.entity.User.UserEntity;
import just.khao.com.entity.registerUser.RegisterUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {
    public void registerUser(RegisterUserEntity registerUserEntity);

    public UserEntity getUserByName(String username);

}
