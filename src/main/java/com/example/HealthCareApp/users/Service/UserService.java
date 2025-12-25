package com.example.HealthCareApp.users.Service;

import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Dto.UpdatePasswordRequest;
import com.example.HealthCareApp.users.Dto.UserDto;
import com.example.HealthCareApp.users.Entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService
{
    UserEntity getCurrentUser();

    Response<UserDto> getMyUserDetails();

    Response<UserDto> getUserById(Long userId);

    Response<List<UserDto>> getAllUsers();

    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    Response<?> uploadProfilePicture(MultipartFile file);

}
