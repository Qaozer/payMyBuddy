package com.payMyBuddy.services;

import com.payMyBuddy.model.Connection;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {
    @Autowired
    private ConnectionRepository conRepository;

    public Connection save(Connection connection){
        return conRepository.save(connection);
    }

    public List<Connection> findAllConnectionsByUser(User user){
        return conRepository.findAllByFirstOrSecond(user, user);
    }

    public Connection setConfirmation(Long id, boolean isConfirmed){
        Connection connection = conRepository.findById(id).get();
        connection.setConfirmed(isConfirmed);
        return save(connection);
    }

    public void delete(Long id){
        conRepository.deleteById(id);
    }
}
