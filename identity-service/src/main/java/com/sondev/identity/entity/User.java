package com.sondev.identity.entity;

import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "varchar(255) COLLATE utf8mb4_unicode_ci")
    String username;

    String password;

    @ManyToMany
    Set<Role> roles;
    /**Hibernate: create table user_roles (user_id varchar(255) not null, roles_name varchar(255) not null, primary key (user_id, roles_name)) engine=InnoDB
     * Hibernate: alter table user_roles add constraint FK6pmbiap985ue1c0qjic44pxlc foreign key (roles_name) references role (name)
     * Hibernate: alter table user_roles add constraint FK55itppkw3i07do3h7qoclqd4k foreign key (user_id) references user (id)
     */

    /*
    @ManyToMany
    Set<Permission> permissions;
    */
    /**Hibernate: create table user_permissions (user_id varchar(255) not null, permissions_name varchar(255) not null, primary key (user_id, permissions_name)) engine=InnoDB
     * Hibernate: alter table user_permissions add constraint FK21bi19y5lo6y6cexroaesds1b foreign key (permissions_name) references permission (name)
     * Hibernate: alter table user_permissions add constraint FK79uqaq5t8qjak65ldagkoo7yr foreign key (user_id) references user (id)
     * */
}
