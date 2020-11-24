package com.payMyBuddy;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class PayMyBuddyApplication {

	@Autowired
	private Environment env;

	private static String user;
	private static String password;

	@Bean
	public ModelMapper modelMapper() {return new ModelMapper();}

	@Bean
	public DataSource getDataSource()
	{
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
		dataSourceBuilder.url("jdbc:mysql://localhost/paymybuddy?serverTimezone=UTC");
		dataSourceBuilder.username(user);
		dataSourceBuilder.password(password);
		return dataSourceBuilder.build();
	}

	private static void login(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter username:");

		try {
			user = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Enter password:");
		try {
			password = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		login();
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}

}
