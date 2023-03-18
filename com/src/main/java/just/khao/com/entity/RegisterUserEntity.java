package just.khao.com.entity;

import lombok.Data;

@Data
public class RegisterUserEntity {
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
}
