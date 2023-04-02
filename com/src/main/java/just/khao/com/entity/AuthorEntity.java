package just.khao.com.entity;

import lombok.Data;

@Data
public class AuthorEntity {
    private Integer user_id;
    private String writer_name;
    private String intro;
    private String about_me;
    private String avatar;
}
