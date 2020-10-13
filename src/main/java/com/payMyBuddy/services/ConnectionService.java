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
        return conRepository.findAllByOwnerOrTarget(user, user);
    }

    public List<Connection> findAllConnectionsByOwner(User user){
        return conRepository.findAllByOwner(user);
    }

    public void delete(Long id){
        conRepository.deleteById(id);
    }

    public Connection createConnection(User owner, User target){
        Connection connection = new Connection();
        connection.setOwner(owner);
        connection.setTarget(target);
        return connection;
    }
}
