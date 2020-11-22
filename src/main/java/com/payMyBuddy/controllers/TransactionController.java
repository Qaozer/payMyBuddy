package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.TransactionDto;
import com.payMyBuddy.services.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TransactionController {
    @Autowired
    private ModelMapper mp;

    @Autowired
    private TransactionService txService;

    /**
     * Process a payment between users
     * @param txDto the transaction infos
     * @return 200 if successful, 400 otherwise
     */
    @PostMapping(value = "transaction")
    public ResponseEntity makePayment (@RequestBody TransactionDto txDto){
        if(txService.makePayment(txDto)){
            return ResponseEntity.ok(txDto);
        } else {
            return ResponseEntity.badRequest().body(txDto);
        }
    }
}
