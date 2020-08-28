package org.launchcode.javawebdevtechjobsauthentication.models.dto;

public class RegisterFormDTO extends LoginFormDTO{
    //Create a register form DTO with the fields above and a field to verify a password.
    private String verifyPassword;

    //Handle the registration data.

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }
}
