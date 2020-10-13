package com.payMyBuddy.services;

import com.payMyBuddy.dto.TransactionDto;
import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.TransactionRepository;
import com.payMyBuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository txRepository;

    @Autowired
    private ConnectionService conService;

    private boolean isPaymentAuthorized(User sender, User receiver, double total){
        return sender.getSolde() > total && conService.findByOwnerAndTarget(sender, receiver).isPresent();
    }

    public boolean makePayment (TransactionDto txDto){
        double amount = txDto.getAmount();
        double fare = amount * 0.05;
        double total = amount + fare;

        User sender = userService.getById(txDto.getSenderId());
        User receiver = userService.getById(txDto.getReceiverId());

        if(isPaymentAuthorized(sender, receiver, total)){
            sender.setSolde(sender.getSolde() - total);
            receiver.setSolde(receiver.getSolde() + amount);
            Transaction transaction = new Transaction();
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAmount(amount);
            transaction.setDate(new Date());
            transaction.setDescription(txDto.getDescription());
            txRepository.save(transaction);
            userService.saveUser(sender);
            userService.saveUser(receiver);
            return true;
        }
        return false;
    }

    public List<Transaction> findAllByUser(User user){
        return txRepository.findAllBySenderOrReceiver(user,user);
    }
}
