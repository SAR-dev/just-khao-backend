package just.khao.com.model;

import lombok.Data;

@Data
public class TokenModel {
    private String access_token;
    private String refresh_token;
}
