package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.NicknameDto;
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
    public ResponseEntity<UserDto> save(@RequestBody User user){
        if(userService.saveUser(user) != null){
            //TODO LOG USER SAVED
            return ResponseEntity.ok(mp.map(user, UserDto.class));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping(value = "/user")
    public ResponseEntity<UserDto> updateNickname(@RequestBody NicknameDto nicknameDto){
        Optional<User> updated = userService.updateNickname(nicknameDto);
        if (updated.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
            return ResponseEntity.ok(mp.map(updated.get(), UserDto.class));
    }
}
