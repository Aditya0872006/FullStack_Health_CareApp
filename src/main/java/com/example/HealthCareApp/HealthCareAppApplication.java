package com.example.HealthCareApp;

import com.example.HealthCareApp.notification.dto.NotificationDto;
import com.example.HealthCareApp.notification.service.NotificationService;
import com.example.HealthCareApp.users.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class HealthCareAppApplication {

//    private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(HealthCareAppApplication.class, args);
	}


//    @Bean
//    CommandLineRunner runner()
//    {
//        return args -> {
//            NotificationDto notificationDTO = NotificationDto.builder()
//					.recipient("sharmaadityamj@gmail.com")
//					.subject("Testing email")
//					.message("Hey, this is a test mail")
//					.build();
//
//			notificationService.sendMail(notificationDTO, new UserEntity());
//        };
//    }
}
