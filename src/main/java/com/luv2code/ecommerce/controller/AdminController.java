package com.luv2code.ecommerce.controller;

import com.luv2code.ecommerce.dto.AdminCreationDTO;
import com.luv2code.ecommerce.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.badRequest;

public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {

        this.adminService = adminService;
    }

    @PreAuthorize("hasAuthority('CREATE_ADMINS')")
    @PostMapping("/create")
    public ResponseEntity addAdmin(@Valid @RequestBody AdminCreationDTO adminCreationDTO) {
        UserDetails userDetails = adminService.loadUserByUsername(adminCreationDTO.getEmail());
        if (userDetails != null) {
//            adminService.addLoginHistory(adminCreationDTO, ip, false);
            return badRequest()
                    .body(new ApiError(null, "username exist.", "username exist."));
        } else {
            adminService.createAdminFullDTO(adminCreationDTO);

            return ResponseEntity.ok(new SuccessfulResponseDTO<>(null, "Admin Created Successfully"));
        }
    }
}
