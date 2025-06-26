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
//        try{
            User user = userDao.getByUserName(principal.getName());
//            if(user == null){
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//            }
//
//
            return profileDao.getByUserId(user.getId());
//            
//        } catch (ResponseStatusException e) {
//            throw new RuntimeException(e);
//        }
    }
    
    // put method
    @PutMapping
    public void updateProfile(@RequestBody Profile profile, Principal principal){
        try{
            User user = userDao.getByUserName(principal.getName());
            if(user == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            
            profileDao.update(profile);

        } catch (ResponseStatusException e) {
            throw new RuntimeException(e);
        }
    }
    
}
