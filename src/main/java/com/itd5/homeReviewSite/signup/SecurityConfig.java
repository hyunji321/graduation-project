package com.itd5.homeReviewSite.signup;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final OAuth2MemberService oAuth2MemberService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .requestMatchers("/api/getCurrentUserId").authenticated() // 현재 로그인한 사용자의 ID를 요청하는 API에 대한 인증 필요
                .requestMatchers("/private/**", "/succession/**", "/review/**","http://localhost:3000/myPage").authenticated()
                .requestMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin() // form login 관련 설정
                .loginPage("/account/login")
                .usernameParameter("name") // Member가 username이라는 파라미터 갖고 있으면 안 적어도 됨.
                .loginProcessingUrl("/login") // 로그인 요청 받는 url
                .defaultSuccessUrl("http://localhost:3000/") // 로그인 성공 후 이동할 url
                .and().logout().permitAll()
                .logoutSuccessUrl("/")
                .and().oauth2Login()//oauth2 관련 설정
                .loginPage("/account/login") //로그인이 필요한데 로그인을 하지 않았다면 이동할 uri 설정
                .defaultSuccessUrl("http://localhost:3000") //OAuth 구글 로그인이 성공하면 이동할 uri 설정
                .userInfoEndpoint()//로그인 완료 후 회원 정보 받기
                .userService(oAuth2MemberService).and().and().build(); //
    }
}
