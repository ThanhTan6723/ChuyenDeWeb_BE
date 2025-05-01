package org.example.chuyendeweb_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phonenumber", length = 15)
    private String phonenumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ColumnDefault("0")
    @Column(name = "failed")
    private Integer failed;

    @ColumnDefault("0")
    @Column(name = "locked")
    private Boolean locked;

    @Column(name = "lock_time")
    private Instant lockTime;

}