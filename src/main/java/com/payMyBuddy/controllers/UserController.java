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

@RestController
public class UserController {

    @Autowired
    private ModelMapper mp;

    @Autowired
    private UserService userService;

    @GetMapping(value ="/user")
    public UserDto getByEmail(@RequestParam("email") String email){
        User user = userService.getByEmail(email);
        if (user != null){
            return mp.map(user, UserDto.class);
        }
        //TODO LOG USER NOT FOUND
        return null;
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
        User updated = userService.updateNickname(nicknameDto);
        if(updated != null){
            return ResponseEntity.ok(mp.map(updated, UserDto.class));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
