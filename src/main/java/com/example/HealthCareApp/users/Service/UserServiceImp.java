package com.example.HealthCareApp.users.Service;

import com.example.HealthCareApp.exception.BadRequestException;
import com.example.HealthCareApp.exception.NotFoundExecption;
import com.example.HealthCareApp.notification.dto.NotificationDto;
import com.example.HealthCareApp.notification.service.NotificationService;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Dto.UpdatePasswordRequest;
import com.example.HealthCareApp.users.Dto.UserDto;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService
{
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Override
    public UserEntity getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null)
        {
            throw new NotFoundExecption("not found");
        }

        String email = authentication.getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new NotFoundExecption("User Not Found"));
    }

    @Override
    public Response<UserDto> getMyUserDetails()
    {
        UserEntity user = getCurrentUser();

        UserDto userDTO = modelMapper.map(user, UserDto.class);

        return Response.<UserDto>builder()
                .statusCode(200)
                .message("User details retrieved successfully.")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<UserDto> getUserById(Long userId)
    {
        UserEntity user = userRepo.findById(userId).orElseThrow(()->new NotFoundExecption("user not found"));
        UserDto userDto=modelMapper.map(user,UserDto.class);

        return Response.<UserDto>builder()
                .statusCode(200)
                .message("user details retrieved successfully")
                .data(userDto)
                .build();

    }

    @Override
    public Response<List<UserDto>> getAllUsers()
    {
        List<UserDto> userDTOS = userRepo.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();


        return Response.<List<UserDto>>builder()
                .statusCode(200)
                .message("All users retrieved successfully.")
                .data(userDTOS)
                .build();

    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest)
    {
        UserEntity user=getCurrentUser();
        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();

        if (oldPassword == null || newPassword == null) {
            throw new BadRequestException("Old and New Password Required");
        }
        // Validate the old password.
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old Password not Correct");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Send password change confirmation email.
        NotificationDto notificationDTO = NotificationDto.builder()
                .recipient(user.getEmail())
                .subject("Your Password Was Successfully Changed")
                .templateName("password-change")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();
        notificationService.sendMail(notificationDTO, user);

        return Response.builder()
                .statusCode(200)
                .message("Password Changed Successfully")
                .build();


    }

    @Override
    public Response<?> uploadProfilePicture(MultipartFile file) {
        return null;
    }
}
