package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("profile")
// this annotation only allows activated users to access these methods
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
public class ProfileController {
    
    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }
    
    // get method
    @GetMapping
    public Profile getprofile(Principal principal){
        try{
            // create a user object from the user dao get by username
            User user = userDao.getByUserName(principal.getName());
            
            // check if the user is null to check if a profile for an existing user exists
            if(user == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            
            // return the user's profile
            return profileDao.getByUserId(user.getId());

        } catch (ResponseStatusException e) {
            throw new RuntimeException(e);
        }
    }
    
    // put method
    @PutMapping
    public Profile updateProfile(@RequestBody Profile profile, Principal principal){
        try{
            User user = userDao.getByUserName(principal.getName());
            if(user == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            
            // take the profile from the request body and associate it with the current user 
            profile.setUserId(user.getId());
            
            // pass the profile into the update method
            profileDao.update(profile);
            
            // return the updated profile
            return profileDao.getByUserId(user.getId());

        } catch (ResponseStatusException e) {
            throw new RuntimeException(e);
        }
    }
    
}
