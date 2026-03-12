package com.marketplace.minimarketplace.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude; import java.time.LocalDateTime; import java.util.Map;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status; private String message; private LocalDateTime timestamp; private Map<String, String> errors;
    public ErrorResponse() {}
    public int getStatus(){return status;} public void setStatus(int v){this.status=v;}
    public String getMessage(){return message;} public void setMessage(String v){this.message=v;}
    public LocalDateTime getTimestamp(){return timestamp;} public void setTimestamp(LocalDateTime v){this.timestamp=v;}
    public Map<String,String> getErrors(){return errors;} public void setErrors(Map<String,String> v){this.errors=v;}
    public static ERBuilder builder(){return new ERBuilder();}
    public static class ERBuilder { private int status; private String message; private LocalDateTime timestamp; private Map<String,String> errors;
        public ERBuilder status(int v){this.status=v;return this;} public ERBuilder message(String v){this.message=v;return this;}
        public ERBuilder timestamp(LocalDateTime v){this.timestamp=v;return this;} public ERBuilder errors(Map<String,String> v){this.errors=v;return this;}
        public ErrorResponse build(){ErrorResponse r=new ErrorResponse(); r.status=status; r.message=message; r.timestamp=timestamp; r.errors=errors; return r;} }
}