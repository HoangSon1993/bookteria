package com.sondev.identity.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidatedToken {
    @Id
    String id; // jwtID

    Date expiryTime; // expirationTime

    /**
     * Hibernate: create table invalidated_token (id varchar(255) not null, expiry_time datetime(6), primary key (id)) engine=InnoDB
     * */
}
