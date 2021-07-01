package com.skyshop300.tacos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configurable
@EnableWebSecurity
// 사용자의 HTTP 요청 경로에 대해 접근 제한과 같은 보안 관리 처리를 설정하는 기능을 제공
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
		.antMatchers("/design", "/orders")
			.access("hasRole('ROLE_USER')")
		.antMatchers("/", "/**").access("permitAll")
		.and()
		.formLogin()			// 커스텀 로그인 폼 호출
		.loginPage("/login")	// 커스텀 로그인 경로 지정
		.and()
		.logout()
		.logoutSuccessUrl("/")	// 로그아웃 이후 특정 페이지로 이동하도록 설정한다.
		.and()
		.csrf().ignoringAntMatchers("/h2-console/**")	// h2 Console의 CSRF 체크 해제
		.and()
		.headers().frameOptions().disable()	// h2 console은 iframe을 사용하기 때문에 X-Frame-Options 비활성화 함.
		;
	}
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder encoder() {	// passwordEncoder 타입 Bean 선언
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
		.userDetailsService(userDetailsService)
		.passwordEncoder(encoder());	// encoder() Bean을 명세 서비스 config에 주입함.
	}
	

	/*
	 * HttpSecurity 추가 설정
	 */
	
	/*
	 * SpEL 활용: 화요일 Taco 생성은 ROLE_USER 권한을 갖는 사용자에게 허용
	 */
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//		.authorizeRequests()
//		.antMatchers("/design", "/orders")
//			.access("hasRole('ROLE_USER') && "
//					+ "T(java.util.Calendar).getInstance().get("
//					+ "T(java.util.Calendar).DAY_OF_WEEK) == "
//					+ "T(java.util.Calendar).TUESDAY")
//		.antMatchers("/", "/**").access("permitAll");
//	}
	
	
	/*
	 * 로그인 경로와 로그인 정보의 커스터마이징
	 */
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//		.authorizeRequests()
//		.antMatchers("/design", "/orders")
//			.access("hasRole('ROLE_USER')")
//		.antMatchers("/", "/**").access("permitAll")
//		.and()
//		.formLogin()			
//		.loginPage("/login")	
//		.loginProcessingUrl("/authenticate")	// 커스텀 URL
//		.usernameParameter("user")				// 커스텀 username 필드
//		.passwordParameter("pwd");				// 커스텀 password 필드
//	}
	
	/*
	 * 커스터마이징
	 */
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//		.authorizeRequests()
//		.antMatchers("/design", "/orders")
//			.access("hasRole('ROLE_USER')")
//		.antMatchers("/", "/**").access("permitAll")
//		.and()
//		.formLogin()	
//		.defaultSuccessUrl("design", true);		// 로그인 완료 후 /design 페이지로 이동
//	}
	

	/*
	 * AuthenticationManagerBuilder 추가 설정
	 */
	
	/*
	 * 인메모리를 활용한 구현
	 */
//	@Override
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication()
//			.withUser("user1")
//			.password("{noop}password1")
//			.authorities("ROLE_USER")
//			.and()
//			.withUser("user2")
//			.password("{noop}passwprd2")
//			.authorities("ROLL_USER");
//	}
	
	
	/* 
	 * JDBC 기반의 사용자 스토어 
	 */
//	@Autowired
//	DataSource dataSource;
//	
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//			.jdbcAuthentication()
//			.dataSource(dataSource)
//			.usersByUsernameQuery(
//				"SELECT USERNAME, PASSWORD, ENABLED "
//				+ "FROM USERS "
//			   + "WHERE USERNAME=?")
//			.authoritiesByUsernameQuery(
//				"SELECT USERNAME, AUTHORITY "
//				+ "FROM AUTHORITIES "
//			   + "WHERE USERNAME=?")
//			//.passwordEncoder(new BCryptPasswordEncoder());
//			.passwordEncoder(new NoEncodingPasswordEncoder());
//	}
	
	
	/* 
	 * LDAP 기반 사용자 스토어
	 */
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//		.ldapAuthentication()
//		.userSearchBase("ou=people")
//		.userSearchFilter("(uid={0})")
//		.groupSearchBase("ou=groups")
//		.groupSearchFilter("member={0}")
//		.contextSource()
//		.root("dc=tacocloud,dc=com")
//		.ldif("classpath:users.ldif")
//		.and()
//		.passwordCompare()
//		.passwordEncoder(new BCryptPasswordEncoder())
//		.passwordAttribute("userPasscode")
//		;
//	}
	
	/*
	 * 
	 */
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//		.ldapAuthentication()
//		.userSearchBase("ou=people")
//		.userSearchFilter("(uid={0})")
//		.groupSearchBase("ou=groups")
//		.groupSearchFilter("member={0}")
//		.passwordCompare()
//		.passwordEncoder(new BCryptPasswordEncoder())
//		.passwordAttribute("userPasscode");
//	}
	
}
