package com.noCompany.BackendStableDiffusionWebApp.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refreshtokens")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @Column(unique = true)
    private String refreshToken;
    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
