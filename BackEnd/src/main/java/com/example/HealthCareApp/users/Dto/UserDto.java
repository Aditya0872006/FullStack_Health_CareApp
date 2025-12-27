package com.example.HealthCareApp.users.Dto;

import com.example.HealthCareApp.role.Entity.RoleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class UserDto
{
    private Long id;

    private String name;

    private String email;

    @JsonIgnore
    private String password;

    private String profilePictureUrl;

    private Set<RoleEntity> roles;
}
