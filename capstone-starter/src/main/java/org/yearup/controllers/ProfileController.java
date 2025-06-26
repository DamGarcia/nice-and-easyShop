package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

@RestController
@RequestMapping("profile")
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
    public Profile getprofile(User user){
        try{
            if(user == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not exist");
            }
            
            return profileDao.getByUserId(user.getId());
            
        } catch (ResponseStatusException e) {
            throw new RuntimeException(e);
        }
    }
    
    // put method
    @PutMapping
    public void updateProfile(@RequestBody Profile profile){
        try{
            if(profile == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user does not exist");
            }
            
            profileDao.update(profile);

        } catch (ResponseStatusException e) {
            throw new RuntimeException(e);
        }
    }
    
}
