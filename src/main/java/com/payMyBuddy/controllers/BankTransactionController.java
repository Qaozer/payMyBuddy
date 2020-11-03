package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.BankTransactionDto;
import com.payMyBuddy.model.BankTransaction;
import com.payMyBuddy.model.User;
import com.payMyBuddy.services.BankTransactionService;
import com.payMyBuddy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BankTransactionController {

    @Autowired
    private BankTransactionService btxService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "bankTransaction/{userId}")
    public ResponseEntity<BankTransaction> processTransaction (@RequestBody BankTransactionDto btxDto, @PathVariable Long userId){
        if(userService.getById(userId).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = userService.getById(userId).get();
        BankTransaction btx = btxService.createBankTransaction(user, btxDto.getiBAN(),btxDto.getAmount());
        if(btxService.processTransaction(btx)){
            return ResponseEntity.ok(btxService.save(btx));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "bankTransaction/{userId}")
    public ResponseEntity<List<BankTransaction>> getTransactions (@PathVariable Long userId){
        if(userService.getById(userId).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = userService.getById(userId).get();
        List<BankTransaction> btxList = btxService.findByUser(user);
        return ResponseEntity.ok(btxList);
    }
}
