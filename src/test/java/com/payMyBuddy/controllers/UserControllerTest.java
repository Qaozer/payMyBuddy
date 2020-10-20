package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.IdentifyDto;
import com.payMyBuddy.dto.NicknameDto;
import com.payMyBuddy.dto.PasswordUpdateDto;
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

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
        //user.setSolde(777);
        return user;
    }

    @Test
    public void saveShouldCreateUserInDbWithSoldSetToZero(){
        User newUser = createUser();
        assertTrue(userRepository.findByEmail(newUser.getEmail()).isEmpty());
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );
        assertTrue(userRepository.findByEmail(newUser.getEmail()).isPresent());
        assertEquals(0, userRepository.findByEmail(newUser.getEmail()).get().getSolde());
        //Verify the password is crypted in DB
        assertNotEquals(newUser.getPassword(),userRepository.findByEmail(newUser.getEmail()).get().getPassword() );
    }

    @Test
    public void saveShoulReturnBadRequestIfEmailAlreadyInDb(){
        User newUser = createUser();
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );
        assertEquals(1, userRepository.findAll().size());
        entity = new HttpEntity<>(newUser, httpHeaders);
        response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //Verify that no other user was created
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    public void updateNicknameShouldUpdateUserIfItExists(){
        User newUser = createUser();
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        User inDb = userRepository.findByEmail(newUser.getEmail()).get();
        assertEquals("BobM",inDb.getNickname());

        NicknameDto nicknameDto = new NicknameDto();
        nicknameDto.setEmail(newUser.getEmail());
        nicknameDto.setNickname("Bobby");

        HttpEntity<NicknameDto> entity2 = new HttpEntity<>(nicknameDto, httpHeaders);
        response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.PUT, entity2, String.class
        );
        assertEquals(nicknameDto.getNickname(), userRepository.findByEmail(newUser.getEmail()).get().getNickname());
    }

    @Test
    public void updateNicknameShouldReturnBadRequestIfUserDoesNotExist(){
        User newUser = createUser();
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        NicknameDto nicknameDto = new NicknameDto();
        nicknameDto.setEmail("Bobby@Aventure.fr");
        nicknameDto.setNickname("Bobby");

        HttpEntity<NicknameDto> entity2 = new HttpEntity<>(nicknameDto, httpHeaders);
        response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.PUT, entity2, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void identifyShouldReturnTrueWhenCredentialsAreCorrect(){
        User newUser = createUser();
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
        User newUser = createUser();
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
        User newUser = createUser();
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
        User newUser = createUser();
        HttpEntity<User> entity = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("user"), HttpMethod.POST, entity, String.class
        );

        String oldHash = userRepository.findByEmail(newUser.getEmail()).get().getPassword();
        PasswordUpdateDto pwUpdDto = new PasswordUpdateDto();
        pwUpdDto.setEmail(newUser.getEmail());
        pwUpdDto.setOldPassword(newUser.getPassword());
        pwUpdDto.setNewPassword("NewMotDePasse");

        HttpEntity<PasswordUpdateDto> entity1 = new HttpEntity<>(pwUpdDto, httpHeaders);
        ResponseEntity response2 = restTemplate.exchange(
                createURLWithPort("/userpw"), HttpMethod.PUT, entity1, String.class
        );

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertNotEquals(oldHash,userRepository.findByEmail(newUser.getEmail()).get().getPassword());
    }

    @Test
    public void updatePasswordShouldReturnBadRequestIfOldPasswordIncorrect(){
        User newUser = createUser();
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
        User newUser = createUser();
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
}
