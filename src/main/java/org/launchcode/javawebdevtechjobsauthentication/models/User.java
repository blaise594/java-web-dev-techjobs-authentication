package org.launchcode.javawebdevtechjobsauthentication.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class User extends AbstractEntity{
    @NotNull
    private String username;

    @NotNull
    private String pwHash;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User(){}

    //Update the constructor that has arguments to encode the password field.
    public User(String username, String password){
        this.username=username;
        this.pwHash=encoder.encode(password);
    }
    //Add a method to check password values.
    public boolean checkIfPasswordMatches(String password){
        return encoder.matches(password, pwHash);
    }
    //add getter for username

    public String getUsername() {
        return username;
    }
}
