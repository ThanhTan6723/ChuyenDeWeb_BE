package org.example.chuyendeweb_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "role")
public class Role {
    @Id
    @Column(name = "id")
    private Long id;
    private String roleName;

}
