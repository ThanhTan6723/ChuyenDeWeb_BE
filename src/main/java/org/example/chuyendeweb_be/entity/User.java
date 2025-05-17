package org.example.chuyendeweb_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", nullable = false, length = 32)
    private String username;

    @Column(name = "password", nullable = false, length = 300)
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public void setLockTime(Instant lockTime) {
        this.lockTime = lockTime;
    }
}