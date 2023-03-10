package com.famillink.famillink.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account{

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean familyCreatedByEmail;
    private boolean familyCreatedByWeb = true;
    private boolean familyEnrollmentResultByEmail;
    private boolean familyEnrollmentResultByWeb = true;
    private boolean familyUpdateByEmail;
    private boolean familyUpdateByWeb = true;
    private LocalDateTime emailCheckTokenGeneratedAt;

    public void generateEmailCheckToken() {

        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
         return this.emailCheckToken.equals(token);
    }

    public boolean coolTimeToSendComfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}
