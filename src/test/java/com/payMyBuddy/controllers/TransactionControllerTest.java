package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.TransactionDto;
import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.TransactionRepository;
import com.payMyBuddy.repositories.UserRepository;
import com.payMyBuddy.services.TransactionService;
import com.payMyBuddy.services.UserService;
import org.junit.Before;
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
public class TransactionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserController userController;

    @Autowired
    private TransactionController transactionController;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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
        user.setSolde(100);
        return user;
    }

    @Before
    public void setup(){
        User user1 = createUser();
        User user2 = createUser();
        user2.setEmail("B@B.fr");

        user1 = userService.createUser(user1);
        user2 = userService.createUser(user2);
    }

    @Test
    public void makePaymentShouldProcessPaymentIfSenderSoldeIsHighEnoughAndThereIsAConnectionBetweenTheUsers(){

        User user1 = userService.getById(1L).get();
        User user2 = userService.getById(2L).get();

        user1.setSolde(100);
        userService.addConnection(user1, user2);
        user1 = userService.saveUser(user1);

        assertTrue(transactionService.findAllByUser(user1).isEmpty());

        TransactionDto txDto = new TransactionDto();

        txDto.setSenderId(user1.getId());
        txDto.setReceiverId(user2.getId());
        txDto.setAmount(80d);
        txDto.setDescription("Une description");

        HttpEntity<TransactionDto> entity = new HttpEntity(txDto, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("transaction"), HttpMethod.POST, entity, TransactionDto.class
        );

        user1 = userService.getById(user1.getId()).get();
        user2 = userService.getById(user2.getId()).get();

        assertFalse(transactionService.findAllByUser(user1).isEmpty());

        Transaction inDB = transactionService.findAllByUser(user1).stream().findFirst().get();
        assertEquals(user1, inDB.getSender());
        assertEquals(user2, inDB.getReceiver());
        assertEquals(16d, user1.getSolde());
        assertEquals(80d, user2.getSolde());
    }

    @Test
    public void makePaymentShouldReturnBadRequestIfTheSenderSoldeIsntHighEnough(){

        User user1 = userService.getById(1L).get();
        User user2 = userService.getById(2L).get();

        assertTrue(transactionService.findAllByUser(user1).isEmpty());

        TransactionDto txDto = new TransactionDto();

        txDto.setSenderId(user1.getId());
        txDto.setReceiverId(user2.getId());
        txDto.setAmount(100d);
        txDto.setDescription("Une description");

        HttpEntity<TransactionDto> entity = new HttpEntity(txDto, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("transaction"), HttpMethod.POST, entity, TransactionDto.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void makePaymentShouldReturnBadRequestIfTheAmountIsZeroOrNegative(){

        User user1 = userService.getById(1L).get();
        User user2 = userService.getById(2L).get();

        assertTrue(transactionService.findAllByUser(user1).isEmpty());

        TransactionDto txDto = new TransactionDto();

        txDto.setSenderId(user1.getId());
        txDto.setReceiverId(user2.getId());
        txDto.setAmount(0);
        txDto.setDescription("Une description");

        HttpEntity<TransactionDto> entity = new HttpEntity(txDto, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("transaction"), HttpMethod.POST, entity, TransactionDto.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void makePaymentShouldReturnBadRequestIfThereIsNoConnectionBetweenUsers(){
        User user1 = userService.getById(1L).get();
        User user2 = userService.getById(2L).get();

        user1.setSolde(100);
        user1 = userService.saveUser(user1);

        assertTrue(transactionService.findAllByUser(user1).isEmpty());

        TransactionDto txDto = new TransactionDto();

        txDto.setSenderId(user1.getId());
        txDto.setReceiverId(user2.getId());
        txDto.setAmount(80d);
        txDto.setDescription("Une description");

        HttpEntity<TransactionDto> entity = new HttpEntity(txDto, httpHeaders);
        ResponseEntity response = restTemplate.exchange(
                createURLWithPort("transaction"), HttpMethod.POST, entity, TransactionDto.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
