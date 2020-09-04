package org.launchcode.javawebdevtechjobsauthentication;

import org.launchcode.javawebdevtechjobsauthentication.controllers.AuthenticationController;
import org.launchcode.javawebdevtechjobsauthentication.models.User;
import org.launchcode.javawebdevtechjobsauthentication.models.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//Have this class inherit from HandlerInterceptorAdapter.
public class AuthenticationFilter extends HandlerInterceptorAdapter {

    //Add autowired instances of both UserRepository and AuthenticationController.
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthenticationController authenticationController;
    //Create a whitelist.
    //In the top of AuthenticationFilter, add a whitelist variable containing the paths that can be accessed without a user session.
    private static final List<String> whitelist = Arrays.asList("/login", "/register", "/logout", "/css");
    //Create a method next that checks a given path against the values in the whitelist.
    private static boolean isWhitelisted(String path) {
        for (String pathRoot : whitelist) {

            if (path.startsWith(pathRoot)) {
                return true;
            }
        }
        return false;
    }

    //Add a preHandle method.
    //This must override the inherited method of the same name.
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        //Update preHandle with a call to this method.
        //Before looking for session and user status, add a conditional that checks the whitelist status of the current request object.
        // Don't require sign-in for whitelisted pages
        if (isWhitelisted(request.getRequestURI())) {
            // returning true indicates that the request may proceed
            return true;
        }
        //Grab the session information from the request object.
        HttpSession session = request.getSession();
        //Query the the session data for a user.
        User user = authenticationController.getUserInfoFromSession(session);

        //If a user exists, return true. Otherwise, redirect to the login page and return false.
        // The user is logged in
        if (user != null) {
            return true;
        }

        // The user is NOT logged in
        response.sendRedirect("/login");
        return false;
    }

}
