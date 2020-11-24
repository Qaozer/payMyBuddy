package com.payMyBuddy.services;

import com.payMyBuddy.model.BankTransaction;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.BankTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BankTransactionService {

    @Autowired
    private BankTransactionRepository bTxRepository;

    @Autowired
    private UserService userService;

    /**
     * Create a BankTransaction object
     * @param user user linked to the transaction
     * @param iBAN IBAN of the bank account
     * @param amount amount of money transferred, can be positive or negative
     * @return the bankTransaction object
     */
    public BankTransaction createBankTransaction(User user, String iBAN, double amount){
        BankTransaction bTx = new BankTransaction();
        bTx.setUser(user);
        bTx.setIban(iBAN);
        bTx.setAmount(amount);
        bTx.setDate(new Date());
        return bTx;
    }

    /**
     * Save the transaction in dB
     * @param btx the transaction
     * @return the transaction in dB
     */
    public BankTransaction save(BankTransaction btx){
        return bTxRepository.save(btx);
    }

    /**
     * Process the bankTransaction
     * @param btx the bankTransaction
     * @return true if successful, false otherwise
     */
    public boolean processTransaction(BankTransaction btx){
        //If the amount is 0, return false
        if(btx.getAmount() == 0){
            return false;
        }

        User user = btx.getUser();
        double absAmount = Math.abs(btx.getAmount());

        //If the amount is > 0, add money to the PayMyBuddy account
        //If it's < 0, withdraw money
        //If the user doesn't have enough money, return false
        if(btx.getAmount() > 0){
            user.setSolde(user.getSolde() + absAmount);
        } else if (btx.getAmount() < 0 && absAmount <= user.getSolde()) {
            user.setSolde(user.getSolde() - absAmount);
        } else {
            return false;
        }

        userService.saveUser(user);
        return true;
    }

    /**
     * Find all transactions linked to a user
     * @param user the user
     * @return a list of transactions
     */
    public List<BankTransaction> findByUser(User user){
        return bTxRepository.findAllByUser(user);
    }
}
