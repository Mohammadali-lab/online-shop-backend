package com.luv2code.ecommerce.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "privilege")
public class Privilege extends BaseEntity {
    @Column(name = "privilege")
    @Enumerated(EnumType.STRING)
    private PrivilegeEnum privilege;

    @OneToMany(mappedBy = "privilege", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true)
    private Set<PrivilegeRole> privilegeRoles = new HashSet<>();

    public void addPrivilegeRole(PrivilegeRole privilegeRole) {
        privilegeRoles.add(privilegeRole);
    }
}
