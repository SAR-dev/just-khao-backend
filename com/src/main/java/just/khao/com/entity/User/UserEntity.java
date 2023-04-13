package just.khao.com.entity.User;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class UserEntity {

    public String user_id;
    public String username;
    public String first_name;
    public String last_name;
    public String email;
    public String password;
    public Timestamp createdAt;
}
