package just.khao.com.model;

import lombok.Data;

@Data
public class ResponseMessage {
    private Integer status = 200;
    private String message = "OK";
    private Object data;
}
