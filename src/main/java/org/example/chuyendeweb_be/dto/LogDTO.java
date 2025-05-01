package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Log}
 */
@Value
public class LogDTO implements Serializable {
    Long id;
    Instant timesTamp;
    String logLevel;
    String module;
    String actionType;
    String logContent;
    String sourceIp;
    String userAgent;
    String affectedTable;
    String beforeData;
    String afterData;
    String national;
}