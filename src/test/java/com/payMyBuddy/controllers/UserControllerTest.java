package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.IdentifyDto;
import com.payMyBuddy.dto.PasswordUpdateDto;
import com.payMyBuddy.dto.UserDto;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional(propagation = Propagation.REQUIRES_NEW)
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

    private User createUser(String email){
        User user = new User();
        user.setNickname("BobM");
        user.setEmail(email);
        user.setPassword("MotDePasse");
        return user;
    }

    @Test
    public void saveShouldCreateUserInDbWithSoldSetToZero(){
        User newUser = createUser("BobM@Aventure.fr");
        assertTrue(userService.getByEmail(newUser.getEmail()).isEmpty());
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );
        assertTrue(userService.getByEmail(newUser.getEmail()).isPresent());
        assertEquals(0, userRepository.findByEmail(newUser.getEmail()).get().getSolde());
        //Verify the password is crypted in DB
        assertNotEquals(newUser.getPassword(),userService.getByEmail(newUser.getEmail()).get().getPassword() );
    }

    @Test
    public void saveShoulReturnBadRequestIfEmailAlreadyInDb(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );
        assertEquals(1, userService.getAll().size());
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //Verify that no other user was created
        assertEquals(1, userService.getAll().size());
    }

    @Test
    public void updateNicknameShouldUpdateUserIfItExists(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        User inDb = userService.getByEmail(newUser.getEmail()).get();
        assertEquals("BobM",inDb.getNickname());

        UserDto nicknameDto = new UserDto();
        nicknameDto.setEmail(newUser.getEmail());
        nicknameDto.setNickname("Bobby");

        HttpEntity<UserDto> entity2 = new HttpEntity<>(nicknameDto, httpHeaders);
        response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.PUT, entity2, String.class
        );
        assertEquals(nicknameDto.getNickname(), userService.getByEmail(newUser.getEmail()).get().getNickname());
    }

    @Test
    public void updateNicknameShouldReturnBadRequestIfUserDoesNotExist(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        UserDto nicknameDto = new UserDto();
        nicknameDto.setEmail("Bobby@Aventure.fr");
        nicknameDto.setNickname("Bobby");

        HttpEntity<UserDto> entity2 = new HttpEntity<>(nicknameDto, httpHeaders);
        response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.PUT, entity2, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void identifyShouldReturnTrueWhenCredentialsAreCorrect(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        IdentifyDto identifyDto = new IdentifyDto();
        identifyDto.setEmail(newUser.getEmail());
        identifyDto.setPassword(newUser.getPassword());

        HttpEntity<IdentifyDto> entity2 = new HttpEntity<>(identifyDto, httpHeaders);
        ResponseEntity response2 = restTemplate.exchange(
                createURLWithPort("/identify"), HttpMethod.POST, entity2, String.class
        );

        assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    public void identifyShouldReturnBadRequestWhenCredentialsAreNotCorrect(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        IdentifyDto identifyDto = new IdentifyDto();
        identifyDto.setEmail(newUser.getEmail());
        identifyDto.setPassword("MauvaisMotDePasse");

        HttpEntity<IdentifyDto> entity2 = new HttpEntity<>(identifyDto, httpHeaders);
        ResponseEntity response2 = restTemplate.exchange(
                createURLWithPort("/identify"), HttpMethod.POST, entity2, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void identifyShouldReturnBadRequestWhenUserEmailIsNotInDb(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        IdentifyDto identifyDto = new IdentifyDto();
        identifyDto.setEmail("Mauvais@Email.com");
        identifyDto.setPassword(newUser.getEmail());

        HttpEntity<IdentifyDto> entity2 = new HttpEntity<>(identifyDto, httpHeaders);
        ResponseEntity response2 = restTemplate.exchange(
                createURLWithPort("/identify"), HttpMethod.POST, entity2, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void updatePasswordShouldModifyPasswordInDbIfCredentialsAreCorrect(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        String oldHash = userService.getByEmail(newUser.getEmail()).get().getPassword();
        PasswordUpdateDto pwUpdDto = new PasswordUpdateDto();
        pwUpdDto.setEmail(newUser.getEmail());
        pwUpdDto.setOldPassword(newUser.getPassword());
        pwUpdDto.setNewPassword("NewMotDePasse");

        HttpEntity<PasswordUpdateDto> entity1 = new HttpEntity<>(pwUpdDto, httpHeaders);
        ResponseEntity response2 = restTemplate.exchange(
                createURLWithPort("/userpw"), HttpMethod.PUT, entity1, String.class
        );

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertNotEquals(oldHash,userService.getByEmail(newUser.getEmail()).get().getPassword());
    }

    @Test
    public void updatePasswordShouldReturnBadRequestIfOldPasswordIncorrect(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        PasswordUpdateDto pwUpdDto = new PasswordUpdateDto();
        pwUpdDto.setEmail(newUser.getEmail());
        pwUpdDto.setOldPassword("AncienMotdePasse");
        pwUpdDto.setNewPassword("NewMotDePasse");

        HttpEntity<PasswordUpdateDto> entity1 = new HttpEntity<>(pwUpdDto, httpHeaders);
        ResponseEntity response2 = restTemplate.exchange(
                createURLWithPort("/userpw"), HttpMethod.PUT, entity1, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void updatePasswordShouldReturnBadRequestIfEmailNotFoundInDb(){
        User newUser = createUser("BobM@Aventure.fr");
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        PasswordUpdateDto pwUpdDto = new PasswordUpdateDto();
        pwUpdDto.setEmail("Wrong@email.com");
        pwUpdDto.setOldPassword(newUser.getPassword());
        pwUpdDto.setNewPassword("NewMotDePasse");

        HttpEntity<PasswordUpdateDto> entity1 = new HttpEntity<>(pwUpdDto, httpHeaders);
        ResponseEntity response2 = restTemplate.exchange(
                createURLWithPort("/userpw"), HttpMethod.PUT, entity1, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void addConnectionShouldCreateConnectionIfTwoUsersExist(){
        User user1 = createUser("BobM@Aventure.fr");
        User user2 = createUser("BobB@Aventure.fr");

        //Create first user in db
        HttpEntity<User> entity = new HttpEntity<>(user1, httpHeaders);
        restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        //Create second user in db
        entity = new HttpEntity<>(user2, httpHeaders);
        restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        //Check that no connection exists
        user1 = userService.getByEmail("BobM@Aventure.fr").get();
        assertTrue(user1.getConnections().isEmpty());

        //Create a connection between user1 and user2 with user 1 as owner
        HttpEntity entity2 = new HttpEntity(httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("/addconnection/1/2"), HttpMethod.PUT, entity, String.class
        );

        //Check the connection has been created
        user1 = userService.getByEmail("BobM@Aventure.fr").get();
        user2 = userService.getByEmail("BobB@Aventure.fr").get();
        assertTrue(user1.getConnections().contains(user2));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void addConnectionShouldReturnBadRequestIfAnyUserDoesNotExist(){
        User user1 = createUser("BobM@Aventure.fr");

        //Create first user in db
        HttpEntity<User> entity = new HttpEntity<>(user1, httpHeaders);
        restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        //Check that no connection exists
        user1 = userService.getByEmail("BobM@Aventure.fr").get();
        assertTrue(user1.getConnections().isEmpty());

        //Create a connection between user1 and user2 with user 1 as owner
        HttpEntity entity2 = new HttpEntity(httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("/addconnection/1/2"), HttpMethod.PUT, entity, String.class
        );

        //Check no connection was created
        user1 = userService.getByEmail("BobM@Aventure.fr").get();
        assertTrue(user1.getConnections().isEmpty());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addConnectionShouldReturnBadRequestIfConnectionAlreadyExists(){
        User user1 = createUser("BobM@Aventure.fr");
        User user2 = createUser("BobB@Aventure.fr");

        //Create first user in db
        HttpEntity<User> entity = new HttpEntity<>(user1, httpHeaders);
        restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        //Create second user in db
        entity = new HttpEntity<>(user2, httpHeaders);
        restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        //Create a connection between user1 and user2 with user 1 as owner
        HttpEntity entity2 = new HttpEntity(httpHeaders);
        restTemplate.exchange(
                createURLWithPort("/addconnection/1/2"), HttpMethod.PUT, entity, String.class
        );

        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("/addconnection/1/2"), HttpMethod.PUT, entity, String.class
        );

        //Check the connection has been created
        user1 = userService.getByEmail("BobM@Aventure.fr").get();
        assertEquals(1, user1.getConnections().size());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
