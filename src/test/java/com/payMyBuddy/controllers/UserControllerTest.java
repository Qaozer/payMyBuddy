package com.payMyBuddy.controllers;

import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.UserRepository;
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
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders httpHeaders = new HttpHeaders();

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
    public void addUserTest(){
        User newUser = createUser();
        assertTrue(userRepository.findByEmail(newUser.getEmail()).isEmpty());
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );
        assertTrue(userRepository.findByEmail(newUser.getEmail()).isPresent());
    }
}
