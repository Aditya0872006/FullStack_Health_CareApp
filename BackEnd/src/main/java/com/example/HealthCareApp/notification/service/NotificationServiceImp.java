package com.example.HealthCareApp.notification.service;

import com.example.HealthCareApp.enums.NotificationType;
import com.example.HealthCareApp.notification.dto.NotificationDto;
import com.example.HealthCareApp.notification.entity.NotificationEntity;
import com.example.HealthCareApp.notification.repository.NotificationRepo;
import com.example.HealthCareApp.users.Entity.UserEntity;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.context.Context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;


import java.nio.charset.StandardCharsets;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService{

    private final NotificationRepo notificationRepo;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendMail(NotificationDto notificationDto, UserEntity user)
    {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(notificationDto.getRecipient());
            helper.setSubject(notificationDto.getSubject());


            // Use template if provided
            if (notificationDto.getTemplateName() != null){

                Context context = new Context();
                context.setVariables(notificationDto.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDto.getTemplateName(), context);

                helper.setText(htmlContent, true);

            }else{
                helper.setText(notificationDto.getMessage(), true);
            }


            javaMailSender.send(mimeMessage);
            log.info("Email sent out");


            //save to our database table
            NotificationEntity notificationToSave = NotificationEntity.builder()
                    .recipient(notificationDto.getRecipient())
                    .subject(notificationDto.getSubject())
                    .message(notificationDto.getMessage())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();

            notificationRepo.save(notificationToSave);

        }catch (Exception e){
            log.info(e.getMessage());
        }

    }

}
