package com.payMyBuddy.services;

import com.payMyBuddy.model.Connection;
import com.payMyBuddy.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectionServiceTest {
    @Autowired
    private ConnectionService conService;

    @Autowired
    private UserService userService;


    private User createUser(){
        User user = new User();
        user.setEmail("A@A.A");
        user.setNickname("AAA");
        user.setSolde(100);
        user.setPassword("AAA");
        return user;
    }

    @Test
    public void createConnectionTest(){

        User user1 = createUser();
        User user2 = createUser();
        user2.setEmail("B@B.B");

        user1 = userService.saveUser(user1);
        user2 = userService.saveUser(user2);

        assertTrue(conService.findAllConnectionsByUser(user1).isEmpty());

        Connection connection = conService.createConnection(user1, user2);

        connection = conService.save(connection);

        assertFalse(conService.findAllConnectionsByUser(user1).isEmpty());
        assertFalse(conService.findAllConnectionsByUser(user2).isEmpty());

        conService.delete(connection.getId());

        assertTrue(conService.findAllConnectionsByUser(user1).isEmpty());
    }
}
