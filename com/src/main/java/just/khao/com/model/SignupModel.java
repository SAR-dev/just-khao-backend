package just.khao.com.model;

import lombok.Data;

@Data
public class SignupModel {
    private String username;
    private String email;
    private String password;
    private String hashed_password;
}
