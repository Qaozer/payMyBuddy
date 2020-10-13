package com.payMyBuddy.controllers;

import com.payMyBuddy.dto.TransactionDto;
import com.payMyBuddy.services.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
    @Autowired
    private ModelMapper mp;

    @Autowired
    private TransactionService txService;

    @PostMapping(value = "transaction")
    public ResponseEntity makePayment (@RequestBody TransactionDto txDto){
        if(txService.makePayment(txDto)){
            //TODO Log Successfull tx
            return ResponseEntity.ok(txDto);
        } else {
            //TODO Log Error during tx
            return ResponseEntity.badRequest().body(txDto);
        }
    }
}
