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
    @Column(name = "log_id", nullable = false)
    private Long id;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "times_tamp")
    private Instant timesTamp;

    @Column(name = "log_level", length = 30)
    private String logLevel;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "log_content", length = 300)
    private String logContent;

    @Column(name = "source_ip", length = 45)
    private String sourceIp;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "affected_table", length = 50)
    private String affectedTable;

    @Lob
    @Column(name = "beforeData")
    private String beforeData;

    @Lob
    @Column(name = "afterData")
    private String afterData;

    @Lob
    @Column(name = "national")
    private String national;

}