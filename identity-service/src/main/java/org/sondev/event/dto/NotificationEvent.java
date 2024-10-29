package org.sondev.event.dto;

import java.util.Map;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    String channel; /* zalo, slack, ... */
    String recipient; /* nguoi nhan */
    String templateCode;
    Map<String, Object> param;

    /* demo */
    String subject;
    String body;
}
