package com.sondev.identity.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    String name;

    String description;

    @ManyToMany
    Set<Permission> permissions;
    /** Hibernate: create table role_permissions (role_name varchar(255) not null, permissions_name varchar(255) not null, primary key (role_name, permissions_name)) engine=InnoDB
     * Hibernate: alter table role_permissions add constraint FKf5aljih4mxtdgalvr7xvngfn1 foreign key (permissions_name) references permission (name)
     * Hibernate: alter table role_permissions add constraint FKcppvu8fk24eqqn6q4hws7ajux foreign key (role_name) references role (name)
     * */
}
