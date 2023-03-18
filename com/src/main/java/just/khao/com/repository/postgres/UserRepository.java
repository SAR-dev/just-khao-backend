package just.khao.com.repository.postgres;

import just.khao.com.entity.RegisterUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {
    public void registerUser(RegisterUserEntity registerUserEntity);

}
