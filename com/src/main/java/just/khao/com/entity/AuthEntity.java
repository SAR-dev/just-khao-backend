package just.khao.com.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AuthEntity {
    private long id;
    private String username;
    private String email;
    private String hashed_password;
    private String refresh_token;
    private Timestamp refreshed_at;
    private Timestamp created_at;
    private Timestamp updated_at;
}
