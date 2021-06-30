**챕터의 목표**

- 스프링시큐리티 자동- 구성하기
- 커스텀 사용자 스토리지 정의하기
- 커스텀 로그인 페이지 만들기
- CSRF 공격으로부터 방어하기
- 사용자 파악하기

# 1. 스프링 시큐리티 활성화하기

- Spring boot Starter Security 의존성만 추가하면, 스프링 시큐리티가 활성화된다.
- 실제 운영  가능한 서비스를 제공하기 위해서는 사용자의 추가 설정이 필요하다.

# 2. 스프링 시큐리티 구성하기
**스프링 시큐리티 적용 확인을 위한 기본 코드**

```java
@Configurable
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
		.antMatchers("/design", "/orders")
			.access("hasRole('ROLE_USER')")
		.antMatchers("/", "/**").access("permitAll")
		.httpBasic();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user1")
			.password("{noop}password1")
			.authorities("ROLE_USER")
			.and()
			.withUser("user2")
			.password("{noop}passwprd2")
			.authorities("ROLL_USER");
	}
```

- SecurityConfig 클래스: HTTP 요청 경로에 대해  접근 제한과 같은 보안 처리를 설정할 수 있게 한다.

**이 장의 요구사항**

한 명 이상의 사용자를 처리할 수 있도록 사용자 정보를 유지, 관리하는 사용자 스토어 구성하기

**스프링 시큐리티에서 제공하는 사용자 스토어 구성 방법**

- 인메모리 사용자 스토어
- JDBC 기반 사용자 스토어
- LDAP 기반 사용자 스토어
- 커스텀 사용자 명세 서비스

## 2.1. 인메모리 사용자 스토어

```java
@Override
public void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.inMemoryAuthentication()
		.withUser("user1")
		.password("{noop}password1")
		.authorities("ROLE_USER")
		.and()
		.withUser("user2")
		.password("{noop}passwprd2")
		.authorities("ROLL_USER");
}
```

- AuthenticationManagerBuilder
    - 인증 명세를 구성하기 위해 Builder 형태의 API 사용한다.
    - 여기서는 inMemoryAuthentication() 메서드를 사용하여 보안 구성 자체에 사용자 정보를 직접 지정하였다.
- {noop}: 스프링5 부터는 비밀번호를 반드시 암호화 해야하는데, 본 코드를 password에 명시하면 비밀번호를 암호화하지 않아도 구동 테스트를 수행할 수 있다.
- 인메모리 기반이므로 서버 종료 시, 기존의 데이터가 삭제 되며, 하드 코딩되어있는 user만 사용이 가능하다.

## 2.2. JDBC 기반의 사용자 스토어

```java
@Configurable
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
...
	@Autowired
	DataSource dataSource;

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.jdbcAuthentication()
			.dataSource(dataSource);
	}
}
```

- AuthenticationManagerBuilder()의 jdbcAuthentication()를 호출한다.
- dataSource: 데이터베이스를 엑세스 방법 자동 주입

**스프링 시큐리티 기본 테이블**

- 사용자 정보: users 테이블
- 권한 정보: authorities 테이블
- 그룹 사용자 정보: group_members 테이블
- 그룹 권한 정보: group_authorities 테이블
- 위 테이블을 테스트용으로 사용하려면, 3장에서와 같이 스키마와 데이터를 sql 파일로 미리 작성하여 수행할 수 있다.

### 2.2.1. 스프링 시큐리티의 기본 사용자 쿼리 대체하기

- 스프링 시큐리티에 지정된 데이터베이스 테이블과 SQL 쿼리를 사용하여 사용자 데이터 베이스를 저장했다면 위와 같이 사용가능
- 테이블이나 열의 이름을 커스터마이징 한다면 다음과 같이 커스터마이징하여 사용한다.

```java
@Autowired
DataSource dataSource;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.jdbcAuthentication()
		.dataSource(dataSource)
		.usersByUsernameQuery(
			"SELECT USERNAME, PASSWORD, ENABLED "
			+ "FROM USERS "
		   + "WHERE USERNAME=?")
		.authoritiesByUsernameQuery(
			"SELECT USERNAME, AUTHORITY "
			+ "FROM AUTHORITIES "
		   + "WHERE USERNAME=?")
		//.passwordEncoder(new BCryptPasswordEncoder());
		.passwordEncoder(new BCryptPasswordEncoder());
}
```

- 스프링 시큐리티 기본 SQL문 참조
    - 위 쿼리 중 사용 테이블의 이름은 데이터베이스 테이블과 달라도 된다.
    - 테이블의 열의 데이터 타입과 길이는 일치해야한다.
- usersByUsernameQuery(), authoritiesByUsernameQuery()
    - 사용자 정보와 권한 쿼리를 대체한다.
- 가장 많이 사용되는 방법

### 2.2.2. 암호화된 비밀번호 사용하기

- BCryptPasswordEncoder
- NoOpPasswordEncoder
- Pbkdf2PasswordEncoder
- SCryptPasswordEncoder
- StandardPasswordEncoder

**src\main\java\tacos\security\NoEncodingPasswordEncoder.java**

```java
public class NoEncodingPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();	// 비밀번호를 암호화하지 않고 String으로 반환
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return rawPassword.toString().equals(encodedPassword); 
	}
	
}
```

```java
@Autowired
DataSource dataSource;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.jdbcAuthentication()
		.dataSource(dataSource)
		.usersByUsernameQuery(
			"SELECT USERNAME, PASSWORD, ENABLED "
			+ "FROM USERS "
		   + "WHERE USERNAME=?")
		.authoritiesByUsernameQuery(
			"SELECT USERNAME, AUTHORITY "
			+ "FROM AUTHORITIES "
		   + "WHERE USERNAME=?")
		//.passwordEncoder(new BCryptPasswordEncoder());
		.passwordEncoder(new NoEncodingPasswordEncoder());
}
```

## 2.3. LDAP 기반 사용자 스토어

**LDAP (**Lightweight Directory Access Protocol**)**

- 네트워크 상에서 조직이나 개인정보 혹은 파일이나 디바이스 정보 등을 찾아보는 것을 가능하게 만든 소프트웨어 프로토콜

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
	.ldapAuthentication()
	.userSearchFilter("(uid={0})")
	.groupSearchFilter("member={0}")
	.groupSearchBase("or=groups")
	.groupSearchFilter("member={0}")
	;
}
```

### 비밀번호 비교 구성하기

- LDAP의 기본 인증 전략은 사용자가 직접 LDAP 서버에서 인증 받는 것이다.
- 기본적으로 LDAP 서버에 있는 userPassword 속성 값과 비교한다.

디폴트 속성(userPassword)값과 비교하는 경우

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
	.ldapAuthentication()
	.userSearchBase("ou=people")
	.userSearchFilter("(uid={0})")
	.groupSearchBase("ou=groups")
	.groupSearchFilter("member={0}")
	.passwordCompare();
}
```

디폴트 속성(userPassword)이 아닌 커스터마이징 값과 비교하는 경우

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
	.ldapAuthentication()
	.userSearchBase("ou=people")
	.userSearchFilter("(uid={0})")
	.groupSearchBase("ou=groups")
	.groupSearchFilter("member={0}")
	.passwordCompare()
	.passwordEncoder(new BCryptPasswordEncoder())
	.passwordAttribute("{커스터마이징 값}");
}
```

### 원격 LDAP 서버 참조하기

- contextSource() 메서드를 이요하여 원격 LDAP 서버 참조하기

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
	.ldapAuthentication()
	.userSearchBase("ou=people")
	.userSearchFilter("(uid={0})")
	.groupSearchBase("ou=groups")
	.groupSearchFilter("member={0}")
	.passwordCompare()
	.passwordEncoder(new BCryptPasswordEncoder())
	.passwordAttribute("userPasscode")
	.and()			
	.contextSource()
	.url("ldap://tacocloud.com:389/dc=tacocloud,dc=com");
}
```

### 내장된 LDAP 서버 구성하기

- 스프링 시큐리티에서 제공하는 내장 LDAP 서버 사용하기
- LDAP 서버가 시작될 때, classpath에서 찾을 수 있는 LDIF(LDAP Data Intercharge Format) 파일로 부터 데이터를 로드한다.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
	.ldapAuthentication()
	.userSearchBase("ou=people")
	.userSearchFilter("(uid={0})")
	.groupSearchBase("ou=groups")
	.groupSearchFilter("member={0}")
	.passwordCompare()
	.passwordEncoder(new BCryptPasswordEncoder())
	.passwordAttribute("userPasscode")
	.and()
	.contextSource()
	.root("dc=tacocloud,dc=com");
}
```

- 스프링이 classpath를 검색하지 않고 직접 지정한 경로에서 LDIF 파일을 찾기

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth
	.ldapAuthentication()
	.userSearchBase("ou=people")
	.userSearchFilter("(uid={0})")
	.groupSearchBase("ou=groups")
	.groupSearchFilter("member={0}")
	.contextSource()
	.root("dc=tacocloud,dc=com")
	.ldif("classpath:users.ldif")
	.and()
	.passwordCompare()
	.passwordEncoder(new BCryptPasswordEncoder())
	.passwordAttribute("userPasscode")
	;
}
```

## 2.4. 사용자 인증 커스터마이징

- 스프링 데이터 리퍼지터리를 사용하여 사용자 인증 수행

### 2.4.1. 사용자 도메인 객체와 퍼시스턴스 정의하기

src\main\java\tacos\User.java

```java
@Entity
@Data
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@RequiredArgsConstructor
public class User implements UserDetails {// Spring Security의 UserDetails 인터페이스 구현
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private final String username;
	private final String password;
	private final String fullname;
	private final String street;
	private final String city;
	private final String state;
	private final String zip;
	private final String phoneNumber;
	
	/*
	 * 해당 사용자에게 부여된 권한을 저장한 컬렉션을 반환함.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
	}
	
	/*
	 * is**Expired(): 사용자 계정의 활성화/비활성화 여부를 나타내는 boolean 값 반환
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
```

- 위 User 클래스는 스프링 시큐리티의 UserDetails 인터페이스를 구현한다.
- UserDetails를 구현한 User 클래스는 기본 사용자 정보를 프레임워크에 제공한다.
    - ex) 사용자에게 부여된 권한, 해당 사용자 계정 사용 가능 여부

src\main\java\tacos\data\UserRepository.java

```java
public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
}
```

- CrudRepository 인터페이스를 확장하여 CRUD 기능 추가
- **username**(id)으로 찾기 메서드 추가 정의

### 2.4.2. 사용자 명세 서비스 생성하기

**스프링 시큐리티의 userDetailsService**

```java
public interface UserDetailsService {
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

**src\main\java\tacos\security\UserRepositoryUserDetailsService.java**

```java
@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {
	private UserRepository userRepo;
	
	@Autowired
	public UserRepositoryUserDetailsService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if(user != null) {
			return user;
		}
		throw new UsernameNotFoundException("User '" + username + "' not found");
	}
	
}
```

- UserDetailsService 인터페이스를 구현
    - 생성자를 통해 UserRepository  인스턴스 주입
    - loadUserByUsername() 메서드에서 주입된 UserRepository 인스턴스의 fundUsername()을 호출하여 User를 탐색
    - loadUserByUsername() 메서드에 username이 인자로 전달되며, 메서드 실행
        - 사용자 이름이 존재: UserDetails 객체 반환
        - 사용자 이름이 미존재(null): UsernameNotFoundException 발생

**src\main\java\tacos\security\SecurityConfig.java**

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	...
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
}
```

- 커스텀 명세 서비스를 스프링 시큐리티에 구현
- SecurityConfig으로 자동 주입된 UserDetailsService 인스턴스를 인자로 전달하여 userDatailsService() 메서드 호출
- 비밀번호 인코더 구성 절차
    1. encoder()에 @Bean 애노테이션이 지정
    2. encoder() 메서드가 생성한 BCryptPasswordEncoder 인스턴스가 스프링 애플리케이션 컨텍스트에 등록/관리
    3. 이 인스턴스가 애플리케이션 컨텍스트로 부터 주입되어 반환

    @@@클래스와 클래스 인스턴스 생성 및 주입의 전 과정을 스프링이 관리하는 @Component와는 의미가 다름

### 2.4.3. 사용자 등록하기

- 스프링 시큐리티는 사용자 등록 절차에 관여하지 않아, 직접 구현해야한다.

**src\main\java\tacos\security\RegistrationController.java**

```java
@Controller
@RequestMapping("/register")
public class RegistrationController {
	private UserRepository userRepo;
	private PasswordEncoder passwordEncoder;
	
	public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping
	public String registerForm() {
		return "registration";
	}
	
	@PostMapping
	public String processRegistration(RegistrationForm form) {	// RegistrationForm 객체가 요청 데이터와 바인딩 됨.
		userRepo.save(form .toUser(passwordEncoder));
		return "redirect:/login";
	}
	
}
```

- UserRepository, PasswordEncoder가 주입 됨.
- registration 뷰의 등록 폼이 제출되면, processRegistration() 메서드에서 HTTP POST 요청이 처리 됨.
- RegistrationForm 객체가 요청 데이터와 자동으로 바인딩 됨.
- **RegistrationController에 주입 된 PasswordEncoder는 SecurityConfig 클래서에 추가 했던 그것과 같은 것이다.**

**src\main\java\tacos\security\RegistrationForm.java**

```java
@Data
public class RegistrationForm {
	
	private String username;
	private String password;
	private String fullname;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String phone;
	
	public User toUser(PasswordEncoder passwordEncoder) {
		return new User(
			username, passwordEncoder.encode(password),
			fullname, street, city, state, zip, phone);
	}
}
```

- toUser()
    - RegistrationForm의 속성 값을 갖는 새로운 User 객체를 생성함.
    - RegistrationController의 processRegistration() 메서드에서 userRepository를 사용하여 저장 됨.
    - 비밀번호가 데이터베이스에 저장 되기 전에 PasswordEncoder 객체를 사용하여 암호화를 수행함

# 3. 웹 요청 보안 처리하기

요구사항

- 타코 디자인 및 주문 로직 처리 이전에 사용자 인증을 수행하고자 함.
- 홈페이지, 로그인 페이지, 등록 페이지는 사용자 인증을 수행하지 아니 함.

위의 보안 규칙을 구성하려면 SecurityConfig 클래스에 config(HttpSecurity http) 메서드를 오버라이딩 해야한다.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	...
}
```

HttpSecurity 인자를 사용하여 구성할 수 있는 것

- HTTP 요청 처리를 허용하기 전에 충족되어야 할 특정 보안 조건을 구성
- 커스텀 로그인 페이지 구성
- 사용자가 애플리케이션의 로그아웃을 할 수 있도록 함
- CSRF 공격으로 부터 보호 설정

## 3.1 웹 요청 보안 처리하기

**src\main\java\tacos\security\SecurityConfig.java**

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
	.authorizeRequests()
	.antMatchers("/design", "/orders")
	.hasRole("ROLE_USER")
	//	.access("hasRole('ROLE_USER')")
	.antMatchers("/", "/**").access("permitAll");
}
```

- /design, /orders 요청만 인증된 사용에게 허용되도록 설정
- 그 이외의 모든 요청은 모든 사용자에게 허용
- 이러한 규칙을 정할 때에는 명시하는 순서에 유의해야 한다.
    - antMatchers()에서 지정된 경로의 패턴 일치를 검사하여, 먼저 지정된 보안 규칙이 우선적으로 처리된다.
    - 따라서, access("permitAll")코드가 먼저 명시되면, hasRole("ROLE_USER")의 효력이 사라진다.
- authorizeRequests()는 ExpressionInterceptUrlRegistry 객체를 반환한다.

    ```java
    public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests()
    			throws Exception {
    		ApplicationContext context = getContext();
    		return getOrApply(new ExpressionUrlAuthorizationConfigurer<>(context))
    				.getRegistry();
    	}
    ```

    - URL 경로와 패턴 및 해당 경로의 보안 요구사항을 구성할 수 있다. 이를 이용하여 위의 옵션을 설정했다.

    - 

|메서드|하는 일|
|-|-|
|access(String)||	인자로 전달된 SpEL 표현식이 true면 접근을 허용한다.|
|anonymous()||	익명의 사용자에게 접근을 허용한다.|
|authenticated()|	익명이 아닌 사용자로 인증된 경우 접근을 허용한다.|
|denyAll()|	무조건 접근을 거부한다.|
|fullyAuthenticated()|	익명이 아니거나 또는 remember-me가 아닌 사용자로 인증되면 접근을 허용한다.|
|hasAnyAuthority(String...)|	지정된 권한 중 어떤 것이라도 사용자가 갖고 있으면 접근을 허용한다.|
|hasAnyRole(String...)|	지정된 역할 중 어느 하나라도 사용자가 갖고 있으면 접근을 허용한다.|
|hasAuthority(String)|	지정된 권한을 사용자가 갖고 있으면 접근을 허용한다.|
|hasIpAddress(String)|	지정된 IP 주소로부터 요청이 오면 접근을 하용한다.|
|hasRole(String)|	지정된 역할을 사용자가 갖고 있으면 접근을 허용한다.|
|not()|	다른 접근 메소드들의 효력을 무효화한다.|
|permitAll()|	무조건 접근을 허용한다.|
|rememberMe()|	remember-me(이전 로그인 정보를 쿠키나 데이터베이스로 저장한 후 일정 기간 내에 다시 접근 시 저장된 정보로 자동 로그인됨)를 통해 인증된 사용자의 접근을 허용한다.|

- 위의 표에서는 대부분의 메서드는 요청 처리의 기본적인 보안 규칙을 제공한다.
- 각 메서드에 정의된 보안 규칙만 사용 가능한 제약이 있다.
- 스프링시큐리티에서는 이에 대한 대안으로 SpEL을 사용할 수 있다.



|보안 표현식|	산출 결과|
|-|-|
|authentication|	해당 사용자의 인증 객체|
|denyAll|	항상 false를 산출한다.|
|hasAnyRole(역할 내역)|	지정된 역할 중 어느 하나라도 해당 사용자가 갖고 있으면 true|
|hasRole(역할)|	지정된 역할을 해당 사용자가 갖고 있으면 true|
|hasIpAddress(IP 주소)|	지정된 IP 주소로부터 해당 요청이 온 것이면 true|
|isAnonymous()|	해당 사용자가 익명 사용자이면 true|
|isAuthenticated()|	해당 사용자가 익명이 아닌 사용자로 인증되었으면 true|
|isFullyAuthenticated()|	해당 사용자가 익명이 아니거나 또는 remember-me가 아닌 사용자로 인증 되었으면 true|
|isRememberMe()|	해당 사용자가 remember-me 기능으로 인증되었으면 true|
|permitAll|	항상 true를 산출한다.|
|principal|	해당 사용자의 principal 객체|


- 대부분의 보안 표현식 확장과 유사한 기능을 사용할 수 있다.

    ```java
    @Override
    	protected void configure(HttpSecurity http) throws Exception {
    		http
    		.authorizeRequests()
    		.antMatchers("/design", "/orders")
    		.access("hasRole('ROLE_USER')")
    		// .hasRole("ROLE_USER")
    		.antMatchers("/", "/**").access("permitAll");
    	}
    ```

- 표현식을 더 유연하게 사용할 수 있다.
    - ex) 화요일 타코 생성은 ROLE_USER 권한을 갖는 사용자에게 허용

        ```java
        @Override
        	protected void configure(HttpSecurity http) throws Exception {
        		http
        		.authorizeRequests()
        		.antMatchers("/design", "/orders")
        			.access("hasRole('ROLE_USER') && "
        					+ "T(java.util.Calendar).getlnstance().get("
        					+ "T(java.util.Calendar).DAY_OF_WEEK) == "
        					+ "T(java.util.Calendar).TUESDAY")
        		.antMatchers("/", "/**").access("permitAll");
        	}
        ```

## 3.2. 커스텀 로그인 페이지 생성하기

**src\main\java\tacos\security\SecurityConfig.java**

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
	.authorizeRequests()
	.antMatchers("/design", "/orders")
		.access("hasRole('ROLE_USER')")
	.antMatchers("/", "/**").access("permitAll")
	.and()
	.formLogin()
	.loginPage("/login");
}
```

- and()
    - 인증 구성이 끝나서 추가적인 HTTP 구성을 적용할 준비가 됨을 나타내는 메서드
    - 새로운 구성을 시작할 때 마다 사용할 수 있다.
- 사용자가 인증되지 않아 로그인이 필요할 경우 커스텀 로그인 폼 경로로 이동 시킨다.
    - formLogin(): 커스텀 로그인 폼 호출
    - loginPage(): 커스텀 로그인 경로 지정

**src\main\java\tacos\web\WebConfig.java**

```java
@Override
public void addViewControllers(ViewControllerRegistry registry) {
	registry.addViewController("/").setViewName("home");
	registry.addViewController("/login");
}
```

- 로그인 페이지는 뷰만 존재하기 때문에 webConfig에 뷰 컨트롤러로 선언한다.

**src\main\resources\templates\login.html**

```java
<form method="POST" th:action="@{/login}" id="loginForm">
  <!-- end::thAction[] -->
  <label for="username">Username: </label>
  <input type="text" name="username" id="username" /><br/>

  <label for="password">Password: </label>
  <input type="password" name="password" id="password" /><br/>

  <input type="submit" value="Login"/>
</form>
```

- 스프링 시큐리티의 로그인 경로와 로그인 정보의 디폴트 설정
    - /login: 로그인 요청처리의 기본 경로.
    - username: 사용자 이름의 필드
    - password: 비밀번호의 필드
- 로그인 경로와 로그인 정보의 커스터마이징

    **src\main\java\tacos\security\SecurityConfig.java**

    ```java
    .and()
    .formLogin()			
    .loginPage("/login")	
    .loginProcessingUrl("/authenticate")	// 커스텀 URL
    .usernameParameter("user")				// 커스텀 username 필드
    .passwordParameter("pwd");				// 커스텀 password 필드
    ```

- ****스프링 시큐리티의 **로그인 완료 후 이동 페이지 디폴트 설정**
    - 스프링 시큐리티는 로그인 후 자동으로 로그인 이전페이지로 이동
- 로그인 완료 후 이동 페이지 커스터마이징

    **src\main\java\tacos\security\SecurityConfig.java**

    ```java
    .and()
    .formLogin()	
    .defaultSuccessUrl("design", true);		// 로그인 완료 후 /design 페이지로 이동
    ```

## 3.3. 로그아웃하기

**src\main\java\tacos\security\SecurityConfig.java**

```java
.and()
logout()
logoutSuccessUrl("/");   //로그아웃 이후 특정 페이지로 이동하도록 설정한다.
```

- /logout POST요청을 가로채는 보안 필터 설정이다.
- 해당 뷰에서 로그아웃 폼과 버튼을 추가해야 한다.

## 3.4 CSRF 공격 방어하기

### CSRF란?

- 사용자가 자신의 의지와는 무관하게 공격자가 의도한 행위(수정, 삭제, 등록 등)를 특정 웹사이트에 요청하게 하는 공격을 말한다.
- 사이트 간 스크립팅(XSS)을 이용한 공격이 사용자가 특정 웹사이트를 신용하는 점을 노린 것이라면, 사이트간 요청 위조는 특정 웹사이트가 사용자의 웹 브라우저를 신용하는 상태를 노린 것이다. 일단 사용자가 웹사이트에 로그인한 상태에서 사이트간 요청 위조 공격 코드가 삽입된 페이지를 열면, 공격 대상이 되는 웹사이트는 위조된 공격 명령이 믿을 수 있는 사용자로부터 발송된 것으로 판단하게 되어 공격에 노출된다.

### CSRF 방어

**방어 과정**

- CSRF 공격을 막기 위해 애플리케이션에서 Form의 hidden 필드에 CSRF 토큰을 삽입한다.
- Form이 제출될 때, Form의 데이터와 함께 서버로 같이 전송된다.
- 서버에서 위 토큰을 원래 생성했던 토큰과 비교한다.
    - 비 일치 시, 악의적인 사이트에서 제출된 것이다.

**스프링 시큐리티의 CSRF 방어**

- 스프링 시큐리티에 내장된 CSRF 방어기능 활용하며, 기본 설정되어 있다.
- CSRF 토큰을 삽입할 _csrf 필드를 form에 포함하면 된다.

    ```java
    <input type="hidden" name="_csrf" th:value="${_csrf.token }"/ >
    ```

- CSRF를 비활성화시킬 수 있지만 권장되지는 않는다.

    ```java
    .and()
    .csrf()
    .disable()
    ```

    - 단, REST API서버 애플리케이션은 CSRF를 disable해야 한다.

# 4. 사용자 인지하기

사용자 주문 데이터를 데이터베이스에 저장할 때 주문이 생성되는 user와 order를 연관시키자.

ex) Order 객체 최초 생성 시, 해당 주문의 User 데이터를 미리 넣을 수 있다.

```java
@Data
@Entity
@Table(name="Taco_Order")
public class Order implements Serializable {
	...
	private Long id;
	private Date placedAt;
	
	@ManyToOne // 한 명의 user는 여러개의 order를 가질 수 있다.
	private User user;
	...
}
```

- @ManyToOne
    - 한 명의 user는 여러개의 order를 가질 수 있다.
- OrderController의 processOrder() 메서드가 주문 저장 로직 수행
- 인증된 사용자가 누구인지 결정한 후, Order 객체의 setUser()를 호출하여 해당 주문을 사용자와 연결하도록 processOrder() 메서드를 수정해야한다.
- 사용자가 누구인지 결정하는 방법은 다음과 같다.
    1. Principal 객체를 Controller 메서드에 주입
    2. Authentication 객체를 컨트롤러 메서드에 주입
    3. @AuthenticationPrincipal 애노테이션을 메서드에 지정
    4. SecurityContextHolder를 사용하여 보안 Context를 얻음

**방법1) Principal 객체를 Controller 메서드에 주입**

```java
@PostMapping
public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Principal principal) {
	if (errors.hasErrors()) {
		return "orderForm";
	}
	
	User user = userRepo.findByUsername(principal.getName());
	order.setUser(user);
	
	orderRepo.save(order);
	sessionStatus.setComplete();
	
	return "redirect:/";
}
```

- 보안과 관련 없는 코드가 혼재된다.

**방법2) Authentication 객체를 인자로 받도록 processOrder() 메서드 변경**

```java
@PostMapping
public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Authentication authentication) {
	if (errors.hasErrors()) {
		return "orderForm";
	}
	
	User user = (User) authentication.getPrincipal();
	order.setUser(user);
	
	orderRepo.save(order);
	sessionStatus.setComplete();
	
	return "redirect:/";
}
```

- authentication.getPrincipal(): Object 타입을 반환하므로 User 타입으로 변환해야한다.

**방법3) processOrder()의 인자로 User 객체를 전달 (본 코드)**

```java
@PostMapping
public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
	if (errors.hasErrors()) {
		return "orderForm";
	}
	
	order.setUser(user);
	
	orderRepo.save(order);
	sessionStatus.setComplete();
	
	return "redirect:/";
}
```

- 타입 변환이 필요 없음
- Authentication과 동일하게 보안 특정 코드만 갖는다.

**방법4) securityContextHolder를 사용하여 보안 Context를 얻기**

```java
@PostMapping
public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus) {
	if (errors.hasErrors()) {
		return "orderForm";
	}
	
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	User user = (User) authentication.getPrincipal();
	order.setUser(user);
	
	orderRepo.save(order);
	sessionStatus.setComplete();
	
	return "redirect:/";
}
```

- 보안 특정 코드가 많음
- Controller 처리 메서드는 물론, 애플리케이션의 어디서든 사용할 수 있다.

### **현재 주문을 하는 인증된 사용자의 이름과 주소를 주문 폼에 미리 채워서 보여주기**

```java
@GetMapping("/current")
public String orderForm(@AuthenticationPrincipal User user, @ModelAttribute Order order) {
	if (order.getDeliveryName() == null) {
		order.setDeliveryName(user.getFullname());
	}
	if (order.getDeliveryStreet() == null) {
		order.setDeliveryStreet(user.getStreet());
	}
	if (order.getDeliveryCity() == null) {
		order.setDeliveryCity(user.getCity());
	}
	if (order.getDeliveryState() == null) {
		order.setDeliveryState(user.getState());
	}
	if (order.getDeliveryZip() == null) {
		order.setDeliveryZip(user.getZip());
	}
	return "orderForm";
}
```

- 인증된 사용자를 메서드 인자로 받아서 해당 사용자의 이름과 주소를 order 객체의 각 속성에 설정한다.

### 타코 생성 뷰에서 현재 사용자의 이름 보여주기

```java

public class DesignTacoController {
...
	private UserRepository userRepo;
	
	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepo, UserRepository userRepo) {
		this.ingredientRepo = ingredientRepo;
		this.tacoRepo = tacoRepo;
		this.userRepo = userRepo;
	}

...
	
	@GetMapping
	public String showDesignForm(Model model, Principal principal) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepo.findAll().forEach(i -> ingredients.add(i));
		
		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),
					filterByType(ingredients, type));
		}
		
//		model.addAttribute("taco", new Taco());
		
		String username = principal.getName();
		User user = userRepo.findByUsername(username);
		model.addAttribute("user", user);
		
		return "design";
	}
...
}
```

- UserRepository의 findByUsername() 메서드를 사용하여 현재 디자인 폼으로 작업 중인 인증된 사용자를 찾는다.

