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
    private TransactionRepository transactionRepository;

    /**
     * Check whether a payment is authorized or not
     * @param sender the user sending the money
     * @param receiver the user receiving the money
     * @param total the amount of money sent + the fee
     * @return true if authorized, false otherwise
     */
    private boolean isPaymentAuthorized(User sender, User receiver, double total){
        return sender.getSolde() > total && sender.getConnections().contains(receiver);
    }

    /**
     * Process a payment
     * @param txDto the transaction infos
     * @return true if successful, false otherwise
     */
    public boolean makePayment (TransactionDto txDto){
        //Check whether both user exist and amount > 0
        if(userRepository.findById(txDto.getSenderId()).isEmpty() || userRepository.findById(txDto.getReceiverId()).isEmpty() ||
        txDto.getAmount() <= 0){
            return false;
        }
        double amount = txDto.getAmount();
        double fare = amount * 0.05;
        double total = amount + fare;

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
            transactionRepository.save(transaction);
            userRepository.save(sender);
            userRepository.save(receiver);
            return true;
        }
        return false;
    }

    /**
     * Find all transactions linked to a user, whether sender or receiver
     * @param user the user
     * @return a list of transactions
     */
    public List<Transaction> findAllByUser(User user){
        return transactionRepository.findAllBySenderOrReceiver(user,user);
    }
}
