package just.khao.com.entity.registerUser;

import lombok.Data;

import javax.annotation.Generated;
import java.util.UUID;

@Data
public class RegisterUserEntity {
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String password;

}
