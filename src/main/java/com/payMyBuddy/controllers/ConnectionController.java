package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.ConnectionDto;
import com.payMyBuddy.model.Connection;
import com.payMyBuddy.model.User;
import com.payMyBuddy.services.ConnectionService;
import com.payMyBuddy.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConnectionController {
    @Autowired
    private ConnectionService conService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mp;

    @PostMapping(value = "/connection")
    public ResponseEntity<Connection> add (@RequestBody final Connection connection){
        Connection con = connection;
        con.setConfirmed(false);
        try {
            conService.save(con);
            return new ResponseEntity<>(con, HttpStatus.CREATED);
        } catch (Exception e){
            //TODO LOG ERROR
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/connection")
    public List<ConnectionDto> getConnectionsByUserEmail(@RequestParam ("email") String email){
        User user = userService.getByEmail(email);
        List<ConnectionDto> connectionDtoList = conService.findAllConnectionsByUser(user).stream()
                .map(this::ConnectionToDto).collect(Collectors.toList());
        return connectionDtoList;
    }

    private ConnectionDto ConnectionToDto (Connection con){
        return mp.map(con, ConnectionDto.class);
    }
}
