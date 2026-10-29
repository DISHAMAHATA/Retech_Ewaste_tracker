package com.retech.dto;

import com.retech.entity.enums.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRoleDto {

    @NotNull(message = "Role is required")
    private Role role;

    private Boolean active;

    public UpdateUserRoleDto() {
    }

    public UpdateUserRoleDto(Role role, Boolean active) {
        this.role = role;
        this.active = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
