package com.collecte_epargne.collecte_epargne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // <--- TRÈS IMPORTANT POU L'NVOI DE MAIL
public class CollecteEpargneApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollecteEpargneApplication.class, args);
	}

}
