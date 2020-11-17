package com.payMyBuddy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payMyBuddy.dto.ConnectionDto;
import com.payMyBuddy.model.Connection;
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
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.REQUIRES_NEW)
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
    public void addConnectionShouldCreateConnectionIfUsersExistsAndConnectionIsNotInDb(){
        User user1 = createUser();
        User user2 = createUser();
        user2.setEmail("B@B.fr");

        user1 = userService.createUser(user1);
        user2 = userService.createUser(user2);

        assertTrue(ctcRepository.findAllByOwnerOrTarget(user1,user1).isEmpty());

        HttpEntity entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("connection/"+user1.getId()+"/"+user2.getId()), HttpMethod.POST,entity, String.class
        );
        assertFalse(ctcService.findByOwnerAndTarget(user1,user2).isEmpty());
    }

    @Test
    public void addConnectionShouldReturnBadRequestIfAnyUserDoesNotExist(){
        User user1 = createUser();
        User user2 = createUser();
        user2.setEmail("B@B.fr");

        user1 = userService.createUser(user1);
        user2 = userService.createUser(user2);

        assertTrue(ctcService.findAllConnectionsByUser(user1).isEmpty());

        HttpEntity entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("connection/"+user1.getId()+"/"+72), HttpMethod.POST,entity, String.class
        );
        assertTrue(ctcService.findAllConnectionsByUser(user1).isEmpty());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addConnectionShouldReturnBadRequestIfConnectionAlreadyExists(){
        User user1 = createUser();
        User user2 = createUser();
        user2.setEmail("B@B.fr");

        user1 = userService.createUser(user1);
        user2 = userService.createUser(user2);

        HttpEntity entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("connection/"+user1.getId()+"/"+user2.getId()), HttpMethod.POST,entity, String.class
        );
        assertFalse(ctcService.findAllConnectionsByUser(user1).isEmpty());

        response = restTemplate.exchange(
                createURLWithPort("connection/"+user1.getId()+"/"+user2.getId()), HttpMethod.POST,entity, String.class
        );

        assertEquals(1, ctcService.findAllConnectionsByOwner(user1).size());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void getConnectionByUserEmailShouldReturnAList(){
        User user1 = createUser();
        User user2 = createUser();
        user2.setEmail("B@B.fr");

        user1 = userService.createUser(user1);
        user2 = userService.createUser(user2);

        HttpEntity entity = new HttpEntity<>(httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("connection/"+user1.getId()+"/"+user2.getId()), HttpMethod.POST,entity, String.class
        );

        ResponseEntity response2 = restTemplate.exchange(
                createURLWithPort("connection?email="+user1.getEmail()), HttpMethod.GET,entity, String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        List<ConnectionDto> list = null;
        try {
            list = mapper.readValue((String)response2.getBody(), new TypeReference<List<ConnectionDto>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertEquals(1, list.size());
        assertEquals("BobM@Aventure.fr", list.get(0).getOwner().getEmail());
        assertEquals("B@B.fr",list.get(0).getTarget().getEmail());
    }

    @Test
    public void getConnectionByUserEmailShouldReturnAnEmptyListIfUserDoesNotExist(){
        HttpEntity entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<List>response = restTemplate.exchange(
                createURLWithPort("connection?email=BobM@Aventurier.fr"), HttpMethod.GET,entity, List.class
        );
        List list = response.getBody();
        assertTrue(list.isEmpty());
    }
}
