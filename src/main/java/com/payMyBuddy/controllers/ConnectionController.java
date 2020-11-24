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
import java.util.Optional;
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

    /**
     * Add a connection between two users
     * @param ownerId the initiating user
     * @param targetId the target user
     * @return 201 CREATED if the connection was successfully added, 400 otherwise
     */
    @PostMapping(value = "/connection/{ownerId}/{targetId}")
    public ResponseEntity<ConnectionDto> add (@PathVariable Long ownerId, @PathVariable Long targetId){
        //If one of the users doesn't exist, return BAD_REQUEST
        if (userService.getById(ownerId).isEmpty() || userService.getById(targetId).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User owner = userService.getById(ownerId).get();
        User slave = userService.getById(targetId).get();
        Connection con = conService.createConnection(owner, slave);
        //If the connection doesn't exist already, save it in DB and return 201
        if(conService.findByOwnerAndTarget(owner, slave).isEmpty()){
            conService.save(con);
            return new ResponseEntity<>(connectionToDto(con), HttpStatus.CREATED);
        }
        //Otherwise, return 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Get a list of connections associated with a user
     * @param email the email of the user
     * @return a list of connections
     */
    @GetMapping(value = "/connection")
    public List<ConnectionDto> getConnectionsByUserEmail(@RequestParam ("email") String email){
        //If user doesn't exist, return an empty list
        if (userService.getByEmail(email).isEmpty()){
            return Collections.emptyList();
        }
        User user = userService.getByEmail(email).get();
        List<ConnectionDto> connectionDtoList = conService.findAllConnectionsByUser(user).stream()
                .map(this::connectionToDto).collect(Collectors.toList());
        return connectionDtoList;
    }

    /**
     * Delete a connection between users
     * @param ctxDto connection infos
     * @return 200 if successful, 400 otherwise
     */
    @DeleteMapping(value = "/connection")
    public ResponseEntity<ConnectionDto> deleteConnectionByEmail(@RequestBody ConnectionDto ctxDto){
        Optional<User> owner = userService.getByEmail(ctxDto.getOwner().getEmail());
        Optional<User> target = userService.getByEmail(ctxDto.getTarget().getEmail());

        if(owner.isPresent() && target.isPresent()){
            Optional<Connection> connection = conService.findByOwnerAndTarget(owner.get(), target.get());
            if (connection.isPresent()){
                conService.delete(connection.get().getId());
                return ResponseEntity.ok(ctxDto);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Connection to ConnectionDto mapper
     * @param con a connection
     * @return corresponding connectionDto
     */
    private ConnectionDto connectionToDto(Connection con){
        return mp.map(con, ConnectionDto.class);
    }
}
