package org.launchcode.javawebdevtechjobsauthentication.controllers;

import org.launchcode.javawebdevtechjobsauthentication.models.User;
import org.launchcode.javawebdevtechjobsauthentication.models.data.UserRepository;
import org.launchcode.javawebdevtechjobsauthentication.models.dto.LoginFormDTO;
import org.launchcode.javawebdevtechjobsauthentication.models.dto.RegisterFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthenticationController {
    @Autowired
    UserRepository userRepository;

    //Add session handling utilities
    //A static final variable for the session key.
    private static final String userSessionKey="user";

    //A method to get the user information from the session.
    public User getUserInfoFromSession(HttpSession session){
        Integer userId = (Integer) session.getAttribute(userSessionKey);
        if (userId==null){
            return null;
        }
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return null;
        }
        return user.get();
    }
    //A method to set the user in the session.
    private static void setUserInSession(HttpSession session, User user){
        session.setAttribute(userSessionKey, user.getId());
    }

    //Add a GET handler in AuthenticationController to display a registration form.
    @GetMapping("/register")
    public String displayRegisterForm(Model model){
        model.addAttribute(new RegisterFormDTO());
        model.addAttribute("title", "Register");
        return "register";
    }

    //Create a POST handler in AuthenticationController to process the form.
    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute @Valid RegisterFormDTO registerFormDTO, Errors errors, HttpServletRequest request, Model model){
        //If the form has validation errors, re-render the registration form with a useful message.
        if(errors.hasErrors()){
            model.addAttribute("title", "Register");
            return "register";
        }
        //If the username is already tied to a user, add a fitting error message and re-render the form.
        User existingUser = userRepository.findByUsername(registerFormDTO.getUsername());
        if(existingUser!=null){
            errors.rejectValue("username","username.alreadyexists", "That username is already in use");
            model.addAttribute("title", "Register");
            return "register";
        }
        //If the two form fields for passwords do not match, add an error message and re-render the form.
        String password = registerFormDTO.getPassword();
        String verifyPassword = registerFormDTO.getVerifyPassword();
        if(!password.equals(verifyPassword)){
            errors.rejectValue("password", "passwords.mismatch", "Passwords do not match");
            model.addAttribute("title", "Register");
            return "register";
        }
        //If none of the above conditions are met,
        //Create a new user with the form data,
        User newUser = new User(registerFormDTO.getUsername(), registerFormDTO.getPassword());
        //Save the user to the database,
        userRepository.save(newUser);
        //Create a new user session,
        setUserInSession(request.getSession(), newUser);
        //Redirect to the home page.
        return "redirect:";
    }

    //Handle the login data.
    @GetMapping("/login")
    public String displayLoginForm(Model model) {
        model.addAttribute(new LoginFormDTO());
        model.addAttribute("title", "Log In");
        return "login";
    }

    @PostMapping("/login")
    public String processLoginForm(@ModelAttribute @Valid LoginFormDTO loginFormDTO,
                                   Errors errors, HttpServletRequest request,
                                   Model model) {
        //If the database does not contain a user with the submitted username, add an error message and re-render.
        if (errors.hasErrors()) {
            model.addAttribute("title", "Log In");
            return "login";
        }

        User theUser = userRepository.findByUsername(loginFormDTO.getUsername());

        if (theUser == null) {
            errors.rejectValue("username", "user.invalid", "The given username does not exist");
            model.addAttribute("title", "Log In");
            return "login";
        }

        //If the submitted password does not match the encoded password attached to the username in the form, add an error message and re-render.
        String password = loginFormDTO.getPassword();

        if (!theUser.checkIfPasswordMatches(password)) {
            errors.rejectValue("password", "password.invalid", "Invalid password");
            model.addAttribute("title", "Log In");
            return "login";
        }
        //If the submission passes all of these checks, create a new user session.
        setUserInSession(request.getSession(), theUser);

        return "redirect:";
    }



    //Handle logging out.
    //Still in AuthenticationController, create a GET handler method for a path to logout.
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        //Invalidate the session data from the request object.
        request.getSession().invalidate();
        //Redirect the user to the login form.
        return "redirect:/login";
    }


}
