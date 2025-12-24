package com.example.HealthCareApp.role.Controller;

import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.role.Entity.RoleEntity;
import com.example.HealthCareApp.role.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
//@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController
{
    private final RoleService roleService;
    @PostMapping
    ResponseEntity<Response<RoleEntity>> createRole(@RequestBody RoleEntity role)
    {
        return ResponseEntity.ok(roleService.creaeRole(role));
    }

    @PutMapping
    ResponseEntity<Response<RoleEntity>> updateRole(@RequestBody RoleEntity role)
    {
        return ResponseEntity.ok(roleService.updateRole(role));
    }

    @GetMapping
    ResponseEntity<Response<List<RoleEntity>>> getRole()
    {
        return ResponseEntity.ok(roleService.getAllRole());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Response<?>> deleteRole(@PathVariable Long id)
    {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }
}
