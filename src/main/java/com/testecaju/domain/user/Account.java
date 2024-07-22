package com.testecaju.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class Account {
    @Id
    @UuidGenerator
    private String id;
    @Column(unique = true)
    private String name;
}
