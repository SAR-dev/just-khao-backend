package just.khao.com.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class IssueTokenModel {
    private long id;
    private String refresh_token;
    private Timestamp refreshed_at;
}
