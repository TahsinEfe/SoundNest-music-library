package com.soundnest.soundnest.Classes;

public class User {
    private String userId;
    private String userName;
    private String email;
    private String password;
    private String createAccount;

    public User(String userId, String userName, String email, String password, String createAccount){
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.createAccount = createAccount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateAccount() {
        return createAccount;
    }

    public void setCreateAccount(String createAccount) {
        this.createAccount = createAccount;
    }
}
