package com.luv2code.ecommerce.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
