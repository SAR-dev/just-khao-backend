package just.khao.com.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserEntity {
    private Integer user_id;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private Date created_at;
}
