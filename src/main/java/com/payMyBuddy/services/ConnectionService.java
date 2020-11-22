package com.payMyBuddy.services;

import com.payMyBuddy.model.Connection;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ConnectionService {
    @Autowired
    private ConnectionRepository conRepository;

    public Connection save(Connection connection){
        return conRepository.save(connection);
    }

    /**
     * Find all connections linked to a user, whether he is owner or target
     * @param user the user
     * @return a list of connections
     */
    public List<Connection> findAllConnectionsByUser(User user){
        return conRepository.findAllByOwnerOrTarget(user, user);
    }

    /**
     * Find all connections a user owns
     * @param user the user
     * @return a list of connections
     */
    public List<Connection> findAllConnectionsByOwner(User user){
        return conRepository.findAllByOwner(user);
    }

    /**
     * Find a connection between two users
     * @param owner the user who owns the connection
     * @param target the user targeted by the connection
     * @return Optional of a connection
     */
    public Optional<Connection> findByOwnerAndTarget(User owner, User target){
        return conRepository.findByOwnerAndTarget(owner, target);
    }

    /**
     * Delete a connection
     * @param id the connection id
     */
    public void delete(Long id){
        conRepository.deleteById(id);
    }

    /**
     * Create a connection object
     * @param owner the user who owns the connection
     * @param target the user targeted
     * @return the connection object
     */
    public Connection createConnection(User owner, User target){
        Connection connection = new Connection();
        connection.setOwner(owner);
        connection.setTarget(target);
        return connection;
    }
}
