package com.example.igfss_client;
import java.io.Serializable;

public class LoginData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String encryptedPassword;

    public LoginData() {
    }

    public LoginData(String email, String encryptedPassword) {
        this.email = email;
        this.encryptedPassword = encryptedPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }
}
