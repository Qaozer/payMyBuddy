package com.payMyBuddy;

import com.payMyBuddy.model.User;
import com.payMyBuddy.repositories.UserRepository;
import com.payMyBuddy.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
class PayMyBuddyApplicationTests {

	@Autowired
	private UserRepository userRepository;

	private static User createUser(){
		User user = new User();
		user.setEmail("A@A.A");
		user.setNickname("AAA");
		user.setSolde(100);
		user.setPassword("AAA");
		return user;
	}

	@Test
	public void unTest(){
		userRepository.save(createUser());
		User found = userRepository.findById(1L).get();
		assertNotNull(found);
		assertEquals(createUser().getNickname(), found.getNickname());
		System.out.println(found.toString());
	}

}
