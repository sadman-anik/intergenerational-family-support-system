package com.example.igfss_client;
import java.io.Serializable;

public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    private int fidn;
    private String spouse1Name;
    private String spouse2Name;
    private String spouse1Phone;
    private String spouse2Phone;
    private String spouse1Email;
    private String spouse2Email;
    private String address;
    private String password;

    public Member() {
    }

    public int getFidn() {
        return fidn;
    }

    public void setFidn(int fidn) {
        this.fidn = fidn;
    }

    public String getSpouse1Name() {
        return spouse1Name;
    }

    public void setSpouse1Name(String spouse1Name) {
        this.spouse1Name = spouse1Name;
    }

    public String getSpouse2Name() {
        return spouse2Name;
    }

    public void setSpouse2Name(String spouse2Name) {
        this.spouse2Name = spouse2Name;
    }

    public String getSpouse1Phone() {
        return spouse1Phone;
    }

    public void setSpouse1Phone(String spouse1Phone) {
        this.spouse1Phone = spouse1Phone;
    }

    public String getSpouse2Phone() {
        return spouse2Phone;
    }

    public void setSpouse2Phone(String spouse2Phone) {
        this.spouse2Phone = spouse2Phone;
    }

    public String getSpouse1Email() {
        return spouse1Email;
    }

    public void setSpouse1Email(String spouse1Email) {
        this.spouse1Email = spouse1Email;
    }

    public String getSpouse2Email() {
        return spouse2Email;
    }

    public void setSpouse2Email(String spouse2Email) {
        this.spouse2Email = spouse2Email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    // This field carries encrypted password from client to server.
    // Server decrypts before saving to database.
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsernameEmail() {
        return spouse1Email;
    }

    @Override
    public String toString() {
        return "FIDN: " + fidn;
    }
}
