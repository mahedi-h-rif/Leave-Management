package com.Leave.Management;

import com.Leave.Management.Entity.leaveAmount;
import com.Leave.Management.Repository.leaveAmountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@OpenAPIDefinition(info = @Info(title = "Library APIS",version = "1.0",description = "Library Management Apis"))
public class ManagementApplication implements CommandLineRunner {

	private final leaveAmountRepository leaveAmountRepository;

	public ManagementApplication(leaveAmountRepository leaveAmountRepository) {
		this.leaveAmountRepository = leaveAmountRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);
	}


	public void run(String... args) throws Exception{
		leaveAmount la =new leaveAmount(
				1L,
				11L,
				22L
		);
		System.out.println(la);
        leaveAmountRepository.save(la);
	}

}
