package just.khao.com.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ProfileEntity {
    private int id;
    private String issuer;
    private String issuer_user_id;
    private String first_name;
    private String last_name;
    private String full_name;
    private String avatar;
    private String contact_email;
    private Boolean email_verified;
    private int auth_id;
    private Timestamp created_at;
    private Timestamp updated_at;
}
