package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.IdentifyDto;
import com.payMyBuddy.dto.NicknameDto;
import com.payMyBuddy.dto.PasswordUpdateDto;
import com.payMyBuddy.dto.UserDto;
import com.payMyBuddy.model.User;
import com.payMyBuddy.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private ModelMapper mp;

    @Autowired
    private UserService userService;

    @GetMapping(value ="/user")
    public ResponseEntity<UserDto> getByEmail(@RequestParam("email") String email){
        Optional<User> user = userService.getByEmail(email);
        if(user.isEmpty()){
            //TODO LOG USER NOT FOUND
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(mp.map(user.get(), UserDto.class));
    }

    @PostMapping(value = "/user")
    public ResponseEntity<UserDto> create(@RequestBody User user){
        if(userService.getByEmail(user.getEmail()).isEmpty()){
            userService.createUser(user);
            return ResponseEntity.ok(mp.map(user, UserDto.class));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/user")
    public ResponseEntity<UserDto> updateNickname(@RequestBody NicknameDto nicknameDto){
        Optional<User> updated = userService.updateNickname(nicknameDto);
        if (updated.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
            return ResponseEntity.ok(mp.map(updated.get(), UserDto.class));
    }

    @PostMapping(value = "/identify")
    public ResponseEntity identify(@RequestBody IdentifyDto identifyDto){
        if(userService.getByEmail(identifyDto.getEmail()).isPresent()){
            if(userService.identify(identifyDto)){
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

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
