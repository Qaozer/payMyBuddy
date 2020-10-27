package com.payMyBuddy.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JbcryptTest {

    @Test
    public void jbCryptTests(){
        String[] mutableHash = new String[1];
        Function<String, Boolean> update = hash -> {mutableHash[0] = hash; return true;};

        //Testing a password
        String hashPw1 = Hashing.hash("password");
        assertTrue(Hashing.verifyAndUpdate("password",hashPw1,update));
        assertFalse(Hashing.verifyAndUpdate("password1",hashPw1,update));

        String hashPw2 = Hashing.hash("password");
        assertTrue(Hashing.verifyAndUpdate("password",hashPw2,update));
        assertFalse(Hashing.verifyAndUpdate("password2",hashPw2,update));

        //Verifying password are the same, but not the hashes
        assertNotEquals(hashPw1, hashPw2);
    }
}
