package com.example.HealthCareApp.security;

import com.example.HealthCareApp.exception.NotFoundExecption;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService
{
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        UserEntity user=userRepo.findByEmail(username).orElseThrow(()->new NotFoundExecption("email not found"));
        return AuthUser.builder().user(user).build();
    }
}
