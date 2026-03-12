package com.marketplace.minimarketplace.dto.response;
public class AuthResponse {
    private String token; private String email; private String role; private String name;
    public AuthResponse() {} public AuthResponse(String token, String email, String role, String name) { this.token=token; this.email=email; this.role=role; this.name=name; }
    public String getToken(){return token;} public String getEmail(){return email;} public String getRole(){return role;} public String getName(){return name;}
    public void setToken(String v){this.token=v;} public void setEmail(String v){this.email=v;} public void setRole(String v){this.role=v;} public void setName(String v){this.name=v;}
    public static ARBuilder builder(){return new ARBuilder();}
    public static class ARBuilder { private String token,email,role,name;
        public ARBuilder token(String v){this.token=v;return this;} public ARBuilder email(String v){this.email=v;return this;}
        public ARBuilder role(String v){this.role=v;return this;} public ARBuilder name(String v){this.name=v;return this;}
        public AuthResponse build(){return new AuthResponse(token,email,role,name);} }
}