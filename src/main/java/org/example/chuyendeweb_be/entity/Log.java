package org.example.chuyendeweb_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant timesTamp;

    private String logLevel;

    private String module;

    private String actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String logContent;

    private String sourceIp;

    private String userAgent;

    private String affectedTable;

    @Lob
    private String beforeData;

    @Lob
    private String afterData;

    @Lob
    private String national;

}