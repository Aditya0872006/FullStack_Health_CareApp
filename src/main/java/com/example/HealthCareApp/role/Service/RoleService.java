package com.example.HealthCareApp.role.Service;

import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.role.Entity.RoleEntity;

import java.util.List;

public interface RoleService
{
    Response<RoleEntity> creaeRole(RoleEntity roleRequest);
    Response<RoleEntity> updateRole(RoleEntity roleRequest);
    Response<List<RoleEntity>> getAllRole();
    Response<?> deleteRole(Long id);
 }
