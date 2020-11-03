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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ConnectionController {
    @Autowired
    private ConnectionService conService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mp;

    @PostMapping(value = "/connection/{ownerId}/{slaveId}")
    public ResponseEntity<Connection> add (@PathVariable Long ownerId, @PathVariable Long slaveId){
        if (userService.getById(ownerId).isEmpty() || userService.getById(slaveId).isEmpty()){
            //TODO Log user doesn't exist
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User owner = userService.getById(ownerId).get();
        User slave = userService.getById(slaveId).get();
        Connection con = conService.createConnection(owner, slave);
        if(conService.findByOwnerAndTarget(owner, slave).isEmpty()){
            conService.save(con);
            return new ResponseEntity<>(con, HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    @GetMapping(value = "/connection")
    public List<ConnectionDto> getConnectionsByUserEmail(@RequestParam ("email") String email){
        if (userService.getByEmail(email).isEmpty()){
            return Collections.emptyList();
        }
        User user = userService.getByEmail(email).get();
        List<ConnectionDto> connectionDtoList = conService.findAllConnectionsByUser(user).stream()
                .map(this::ConnectionToDto).collect(Collectors.toList());
        return connectionDtoList;
    }

    private ConnectionDto ConnectionToDto (Connection con){
        return mp.map(con, ConnectionDto.class);
    }
}
