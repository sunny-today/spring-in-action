package com.jyjeong.tacos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/design", "/orders").access("hasRole('ROLE_USER')")
//                .antMatchers("/","/**","/h2-console/**").permitAll()
//                .and()
//                .httpBasic();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // h2 console이 csrf 설정이 안먹히기 때문에 security 설정을 배포 환경에 따라 분리
//        if(isLocalMode()) {
//            setLocalMode(http);
//        } else {
//            setRealMode(http);
//        }
        setLocalMode(http);
    }

//    private boolean isLocalMode() {
//        String profile = env.getActiveProfiles().length > 0? env.getActiveProfiles()[0] : "local";
//        return profile.equals("local");
//    }

    private void setLocalMode(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/me", "/h2-console/**", "/login/**", "/js/**", "/css/**", "/image/**", "/fonts/**", "/favicon.ico").permitAll()
                .and().headers().frameOptions().sameOrigin()
                .and().csrf().disable()
        ;
    }

//    private void setRealMode(HttpSecurity http) throws Exception {
//        http.antMatcher("/**")
//                .authorizeRequests()
//
//                .and().csrf().csrfTokenRepository(csrfTokenRepository())
//                .and().addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
//        ;
//    }

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

//        auth.inMemoryAuthentication()
//                .withUser("user1")
//                .password("{noop}password1")
//                .authorities("ROLE_USER")
//                .and()
//                .withUser("user2")
//                .password("{noop}password2")
//                .authorities("ROLE_USER");

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "SELECT USERNAME, PASSWORD, ENABLED FROM USERS " +
                        "WHERE USERNAME = ?")
                .authoritiesByUsernameQuery(
                        "SELECT USERNAME, AUTHORITY FROM AUTHORITIES " +
                        "WHERE USERNAME = ?")
                //.passwordEncoder(new BCryptPasswordEncoder());
                .passwordEncoder(new NoEncodingPasswordEncoder());
    }
}
