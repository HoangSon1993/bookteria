package org.sondev.notification.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailRequest {
    Recipient to; /* chỉ cho phép gửi tới 1 địa c chỉ. */
    String subject;
    String htmlContent;
}
