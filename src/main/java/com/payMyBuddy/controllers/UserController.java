package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.IdentifyDto;
import com.payMyBuddy.dto.PasswordUpdateDto;
import com.payMyBuddy.dto.UserDto;
import com.payMyBuddy.model.User;
import com.payMyBuddy.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserController {

    @Autowired
    private ModelMapper mp;

    @Autowired
    private UserService userService;

    /**
     * Get a user by its email
     * @param email the user's email
     * @return a userDto, BAD_REQUEST if the user doesn't exist.
     */
    @GetMapping(value ="/user")
    public ResponseEntity<UserDto> getByEmail(@RequestParam("email") String email){
        Optional<User> user = userService.getByEmail(email);
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(mp.map(user.get(), UserDto.class));
    }

    /**
     * Create a new user in DB
     * @param user the user to be added in DB
     * @return 200 if successful, 400 otherwise
     */
    @PostMapping(value = "/user")
    public ResponseEntity<UserDto> create(@RequestBody User user){
        //Only create the user if its email isn't already in DB
        if(userService.getByEmail(user.getEmail()).isEmpty()){
            userService.createUser(user);
            return ResponseEntity.ok(mp.map(user, UserDto.class));
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Update a user's nickname
     * @param userDto user's email and new nickname
     * @return 200 if successful, 400 otherwise
     */
    @PutMapping(value = "/user")
    public ResponseEntity<UserDto> updateNickname(@RequestBody UserDto userDto){
        if(userService.getByEmail(userDto.getEmail()).isPresent()){
            userService.updateNickname(userDto);
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Verify the identity of the user with its credentials
     * @param identifyDto contains user's email and password sent for verification
     * @return 200 if credentials are matching, 400 otherwise
     */
    @PostMapping(value = "/identify")
    public ResponseEntity identify(@RequestBody IdentifyDto identifyDto){
        if(userService.getByEmail(identifyDto.getEmail()).isPresent()){
            if(userService.identify(identifyDto)){
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Update a user's password
     * @param pwUpdDto contains the email, old password and new password
     * @return 200 if successful, 400 otherwise
     */
    @PutMapping(value = "/userpw")
    public ResponseEntity updatePassword(@RequestBody PasswordUpdateDto pwUpdDto){
        if (userService.getByEmail(pwUpdDto.getEmail()).isPresent()){
            if(userService.updatePassword(pwUpdDto)){
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
