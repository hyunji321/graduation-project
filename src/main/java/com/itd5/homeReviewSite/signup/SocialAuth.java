package com.itd5.homeReviewSite.signup;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SocialAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //기본키
    private String name; //유저 이름
    private String password; //유저 비밀번호
    private String email; //유저 구글 이메일
    private String role; //유저 권한 (일반 유저, 관리자)
    private String provider; //공급자 (google, facebook ...)
    private String providerId; //공급 아이디
    private String nickname;
    private String userinfo;
    @Builder
    public SocialAuth(String name, String password, String email, String role, String provider, String providerId, String nickname) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.nickname = nickname;
    }
    public void setUserInfo(String userinfo) {
        this.userinfo = userinfo;
    }
}