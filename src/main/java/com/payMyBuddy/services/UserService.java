package com.payMyBuddy.services;

import com.payMyBuddy.dto.IdentifyDto;
import com.payMyBuddy.dto.NicknameDto;
import com.payMyBuddy.dto.PasswordUpdateDto;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.UserRepository;
import com.payMyBuddy.utils.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user){
        String hashPw = Hashing.hash(user.getPassword());
        user.setPassword(hashPw);
        user.setSolde(0d);
        return userRepository.save(user);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public Optional<User> getByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> updateNickname (NicknameDto nicknameDto){
        Optional<User> opt = userRepository.findByEmail(nicknameDto.getEmail());
        if(opt.isPresent()){
            User inDB = opt.get();
            if(!inDB.getNickname().equals(nicknameDto.getNickname())){
                inDB.setNickname(nicknameDto.getNickname());
                inDB = userRepository.save(inDB);
            }
            return (Optional.of(inDB));
        }
        return opt;
    }

    public Optional<User> getById (Long id) {
        return userRepository.findById(id);
    }

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
}
