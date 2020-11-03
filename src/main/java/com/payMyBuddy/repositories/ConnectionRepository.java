package com.payMyBuddy.repositories;

import com.payMyBuddy.model.Connection;
import com.payMyBuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    Optional<Connection> findById(Long id);
    List<Connection> findAllByOwnerOrTarget(User owner, User target);
    List<Connection> findAllByOwner(User owner);
    Optional<Connection> findByOwnerAndTarget(User owner, User target);
}
