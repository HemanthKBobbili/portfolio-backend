package com.hkb.portfolio_backend;

import com.hkb.portfolio_backend.repository.ProjectRepository;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableBatchProcessing
public class PortfolioBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProjectRepository repository){
		return args -> {
			System.out.println("Sample Projects in DB: " + repository.count());
		};
	}

}
