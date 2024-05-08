package com.luv2code.ecommerce.entity;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@NoArgsConstructor
@Getter
@Table(name = "role")
public class Role extends BaseEntity{

    @Column(name = "english_name", unique = true)
    private String englishName;

    @Column(name = "persian_name", unique = true)
    private String persianName;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "role", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Admin> admins = new HashSet<>();

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER,cascade = {CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true)
    private Set<PrivilegeRole> privilegeRoles = new HashSet<>();

    public void addAdmin(Admin admin) {
        admins.add(admin);
        admin.setRole(this);
    }

    public void addPrivilegeRole(PrivilegeRole privilegeRole) {
        privilegeRoles.add(privilegeRole);
    }
}
