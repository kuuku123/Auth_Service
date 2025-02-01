package org.example.auth_service.config.mail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {

    private String to;

    private String from;

    private String subject;

    private String message;
}
