package com.NMCNPM.ABT_bio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvalidatedToken {
    @Id
    private String id;
    private Date expiryTime;
}
