package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.BankTransactionDto;
import com.payMyBuddy.model.BankTransaction;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.BankTransactionRepository;
import com.payMyBuddy.repositories.UserRepository;
import com.payMyBuddy.services.BankTransactionService;
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
public class BankTransactionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankTransactionService btxService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankTransactionRepository btxRepository;

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
        user.setSolde(0);
        return user;
    }

    @Test
    public void processTransactionShouldGoThroughWhenMoneyIsDepositedOnTheAppAccount(){
        User user1 = createUser();
        user1 = userService.saveUser(user1);

        assertTrue(btxService.findByUser(user1).isEmpty());

        BankTransactionDto btxDto = new BankTransactionDto();

        btxDto.setAmount(100d);
        btxDto.setiBAN("IBAN");

        HttpEntity<BankTransactionDto> entity = new HttpEntity<>(btxDto, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("bankTransaction/"+user1.getId()), HttpMethod.POST,entity, BankTransaction.class
        );

        assertFalse(btxService.findByUser(user1).isEmpty());
        assertTrue(userService.getById(user1.getId()).get().getSolde() == 100);
    }

    @Test
    public void processTransactionShouldGoThroughWhenMoneyIsTakenFromTheAppAccountAndTheSoldeIsEnough(){
        User user1 = createUser();
        user1.setSolde(100);
        user1 = userService.saveUser(user1);

        assertTrue(btxService.findByUser(user1).isEmpty());

        BankTransactionDto btxDto = new BankTransactionDto();

        btxDto.setAmount(-90d);
        btxDto.setiBAN("IBAN");

        HttpEntity<BankTransactionDto> entity = new HttpEntity<>(btxDto, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("bankTransaction/"+user1.getId()), HttpMethod.POST,entity, BankTransaction.class
        );

        assertFalse(btxService.findByUser(user1).isEmpty());
        assertTrue(userService.getById(user1.getId()).get().getSolde() == 10);
    }

    @Test
    public void processTransactionShouldReturnBadRequestWhenMoneyIsTakenFromTheAppAccountAndTheSoldeIsntEnough(){
        User user1 = createUser();
        user1.setSolde(10);
        user1 = userService.saveUser(user1);

        assertTrue(btxService.findByUser(user1).isEmpty());

        BankTransactionDto btxDto = new BankTransactionDto();

        btxDto.setAmount(-90d);
        btxDto.setiBAN("IBAN");

        HttpEntity<BankTransactionDto> entity = new HttpEntity<>(btxDto, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("bankTransaction/"+user1.getId()), HttpMethod.POST,entity, BankTransaction.class
        );

        assertTrue(btxService.findByUser(user1).isEmpty());
        assertTrue(userService.getById(user1.getId()).get().getSolde() == 10);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void processTransactionShouldReturnBadRequestWhenTheUserIdIsIncorrect(){
        User user1 = createUser();
        user1.setSolde(10);
        user1 = userService.saveUser(user1);

        assertTrue(btxService.findByUser(user1).isEmpty());

        BankTransactionDto btxDto = new BankTransactionDto();

        btxDto.setAmount(10d);
        btxDto.setiBAN("IBAN");

        HttpEntity<BankTransactionDto> entity = new HttpEntity<>(btxDto, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("bankTransaction/"+(user1.getId()+1)), HttpMethod.POST,entity, BankTransaction.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
