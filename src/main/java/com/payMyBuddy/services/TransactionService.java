package com.payMyBuddy.services;

import com.payMyBuddy.dto.TransactionDto;
import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.TransactionRepository;
import com.payMyBuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

        if(userRepository.findById(txDto.getSenderId()).isEmpty() || userRepository.findById(txDto.getReceiverId()).isEmpty()){
            //TODO Log error : Bad user id
            return false;
        }

        User sender = userRepository.findById(txDto.getSenderId()).get();
        User receiver = userRepository.findById(txDto.getReceiverId()).get();

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
            userRepository.save(sender);
            userRepository.save(receiver);
            return true;
        }
        return false;
    }

    public List<Transaction> findAllByUser(User user){
        return txRepository.findAllBySenderOrReceiver(user,user);
    }
}
