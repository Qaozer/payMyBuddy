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

    public BankTransaction createBankTransaction(User user, String iBAN, double amount){
        BankTransaction bTx = new BankTransaction();
        bTx.setUser(user);
        bTx.setIBAN(iBAN);
        bTx.setAmount(amount);
        bTx.setDate(new Date());
        return bTx;
    }

    public BankTransaction save(BankTransaction btx){
        return bTxRepository.save(btx);
    }

    public boolean processTransaction(BankTransaction btx){
        if(btx.getAmount() == 0){
            return false;
        }

        User user = btx.getUser();
        double absAmount = Math.abs(btx.getAmount());

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

    public List<BankTransaction> findByUser(User user){
        return bTxRepository.findAllByUser(user);
    }
}
