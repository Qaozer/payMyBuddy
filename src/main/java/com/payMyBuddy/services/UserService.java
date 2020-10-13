package com.payMyBuddy.services;

import com.payMyBuddy.dto.NicknameDto;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user){
        userRepository.save(user);
        return user;
    }

    public User getByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public User updateNickname (NicknameDto nicknameDto){
        Optional<User> opt = userRepository.findByEmail(nicknameDto.getEmail());
        if(opt.isPresent()){
            User inDB = opt.get();
            inDB.setNickname(nicknameDto.getNickname());
            userRepository.save(inDB);
            return (inDB);
        }
        return null;
    }

    public User getById (Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
