package com.payMyBuddy.services;

import com.payMyBuddy.dto.IdentifyDto;
import com.payMyBuddy.dto.PasswordUpdateDto;
import com.payMyBuddy.dto.UserDto;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.UserRepository;
import com.payMyBuddy.utils.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user in DB
     * @param user the user
     * @return the user in DB
     */
    public User createUser(User user){
        String hashPw = Hashing.hash(user.getPassword());
        user.setPassword(hashPw);
        user.setSolde(0d);
        return userRepository.save(user);
    }

    /**
     * Save modifications to a user
     * @param user the user
     * @return the user
     */
    public User saveUser(User user){
        return userRepository.save(user);
    }

    /**
     * Find a user by its email
     * @param email the email
     * @return optional of a user
     */
    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    /**
     * Update a user's nickname
     * @param UserDto updated infos
     */
    public void updateNickname (UserDto UserDto){
        Optional<User> opt = userRepository.findByEmail(UserDto.getEmail());
        if(opt.isPresent()){
            User inDB = opt.get();
            if(!inDB.getNickname().equals(UserDto.getNickname())){
                inDB.setNickname(UserDto.getNickname());
                userRepository.save(inDB);
            }
        }
    }

    /**
     * Find a user by its id
     * @param id the user id
     * @return optional of the user
     */
    public Optional<User> getById (Long id) {
        return userRepository.findById(id);
    }

    /**
     * Verify the identity of a user and update the hash of the password if necessary
     * @param identifyDto credentials sent
     * @return true if credentials match, false otherwise
     */
    public boolean identify(IdentifyDto identifyDto){
        User user = getByEmail(identifyDto.getEmail()).get();
        String[] newHash = new String[1];
        Function<String, Boolean> update = hash -> {newHash[0] = hash; return true;};
        if(Hashing.verifyAndUpdate(identifyDto.getPassword(), user.getPassword(), update)){
            if(newHash[0] != null && !newHash[0].equals(user.getPassword())){
                user.setPassword(newHash[0]);
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }

    /**
     * Update the password of a user
     * @param pwUpdDto contains user email, old and new password
     * @return true if successful, false otherwise
     */
    public boolean updatePassword(PasswordUpdateDto pwUpdDto){
        Optional<User> opt = userRepository.findByEmail(pwUpdDto.getEmail());
        if (opt.isPresent()){
            User user = opt.get();
            if(Hashing.verify(pwUpdDto.getOldPassword(), user.getPassword())){
                user.setPassword(Hashing.hash(pwUpdDto.getNewPassword()));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Get all users
     * @return a list of all users in DB
     */
    public List<User> getAll(){
        return userRepository.findAll();
    }
}
