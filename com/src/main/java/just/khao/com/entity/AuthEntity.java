package just.khao.com.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class AuthEntity {
    private int id;
    private String username;
    private String email;
    private Boolean verified;
    private String hashed_password;
    private String refresh_token;
    private Instant refreshed_at;
    private Timestamp created_at;
    private Timestamp updated_at;
}
