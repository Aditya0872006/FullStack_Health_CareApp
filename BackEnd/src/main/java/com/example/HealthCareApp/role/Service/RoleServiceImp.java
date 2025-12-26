package com.example.HealthCareApp.role.Service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.role.Entity.RoleEntity;
import com.example.HealthCareApp.role.Repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService
{
    private final RoleRepo roleRepo;

    @Override
    public Response<RoleEntity> creaeRole(RoleEntity roleRequest)
    {
        RoleEntity role=roleRepo.save(roleRequest);
        return Response.<RoleEntity>builder()
            .statusCode(HttpStatus.OK.value())
            .message("Role Saved Successfully")
            .data(role)
            .build();

    }

    @Override
    public Response<RoleEntity> updateRole(RoleEntity roleRequest)
    {
        RoleEntity role = roleRepo.findById(roleRequest.getId())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        role.setName(roleRequest.getName());

        RoleEntity updatedRole = roleRepo.save(role);
        return Response.<RoleEntity>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
    }

    @Override
    public Response<List<RoleEntity>> getAllRole()
    {
        List<RoleEntity> roles = roleRepo.findAll();

        return Response.<List<RoleEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All Role Find Succesfully")
                .data(roles)
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id)
    {
        if (!roleRepo.existsById(id)) {
            throw new NotFoundException("Role Not Found");
        }

        roleRepo.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build();

    }
}
