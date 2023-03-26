package just.khao.com.entity;

import lombok.Data;

@Data
public class GoogleToken {
    private String sub;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String email;
    private boolean email_verified;
}
