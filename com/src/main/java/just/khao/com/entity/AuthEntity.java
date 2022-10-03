package just.khao.com.entity;

import lombok.Data;

@Data
public class AuthEntity {
    private long id;
    private String username;
    private String email;
    private String hashed_password;
}
