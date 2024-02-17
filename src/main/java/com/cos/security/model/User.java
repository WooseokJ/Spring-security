package com.cos.security.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role; // ROLE_USER, ROLE_ADMIN


    @CreatedDate
    private LocalDateTime createdDate;


    @LastModifiedDate // 마지막 수정날자 (휴먼계정인지 판단하기위함)
    private LocalDateTime lastModifyDate;

    private String provider;
    private String providerId;

    @Builder // 빌더패턴통해 만들거임.
    public User(String username, String password, String email, String role, LocalDateTime createdDate, LocalDateTime lastModifyDate, String provider, String providerId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.createdDate = createdDate;
        this.lastModifyDate = lastModifyDate;
        this.provider = provider;
        this.providerId = providerId;
    }
}
