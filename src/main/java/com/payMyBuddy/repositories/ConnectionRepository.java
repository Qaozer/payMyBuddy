package com.payMyBuddy.repositories;

import com.payMyBuddy.model.Connection;
import com.payMyBuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    Optional<Connection> findById(Long id);
    List<Connection> findAllByFirstOrSecond(User first, User second);
}
