package com.payMyBuddy.controllers;

import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.ConnectionRepository;
import com.payMyBuddy.repositories.UserRepository;
import com.payMyBuddy.services.ConnectionService;
import com.payMyBuddy.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private UserController userController;

    @Autowired
    private ConnectionController ctcController;

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionService ctcService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository ctcRepository;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders httpHeaders = new HttpHeaders();

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private User createUser(){
        User user = new User();
        user.setNickname("BobM");
        user.setEmail("BobM@Aventure.fr");
        user.setPassword("MotDePasse");
        user.setSolde(777);
        return user;
    }

    @Test
    public void addConnectionTest(){
        User user1 = createUser();
        User user2 = createUser();
        user2.setEmail("B@B.fr");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        assertTrue(ctcRepository.findAllByOwnerOrTarget(user1,user1).isEmpty());

        HttpEntity entity = new HttpEntity<>(httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("connection/"+user1.getId()+"/"+user2.getId()), HttpMethod.POST,entity, String.class
        );
        assertFalse(ctcRepository.findAllByOwnerOrTarget(user1,user1).isEmpty());
    }
}
