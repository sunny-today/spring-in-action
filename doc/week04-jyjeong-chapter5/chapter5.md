## CHAPTRER 5. 구성 속성 사용하기
<hr>
#. Chapter5's Goal<br>
1. 자동-구성되는 빈 조정하기<br>
2. 구성 속성을 애플리케이션 컴포넌트에 적용하기<br>
3. 스프링 프로파일 사용하기<br>
<hr>

## 5.1 자동-구성 세부 조정하기
- 빈 연결(Bean wiring) - 빈으로 생성되는 애플리케이션 컴포넌트 및 상호 간에 주입되는 방법을 선언하는 구성
- 속성 주입(Property Injection) - 스프링 애플리케이션 컨텍스트에서 빈의 속성 값을 설정하는 구성
```java
@Bean
public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("schema.sql")
        .addScripts("user_data.sql", "ingredient_data.sql")
        .build();
}
```
- EmbeddedDatabaseBuilder - Embedded Database(H2, HSQL, DERBY)를 구성하는 클래스
- addScript() / addScripts() 메서드는 하나 또는 여러 개의 속성을 SQL 스크립트 파일의 이름으로 설정
- 만일 스프링 부트를 사용하지 않았다면, DataSource 빈을 구성해주어야 하지만, 스프링부트를 사용 중일 때는 Auto-Configuration이 DataSource 빈을 구성해 주어 상기의 Bean 구성이 필요가 없다.

> Runtime 시 classpath에서 h2 Library를 찾아 사용할 수 있다면 스프링 부트는 해당 빈을 자동으로 찾아 스프링 애플리케이션 컨텍스트에 생성한다. <br>
> data.sql, schema.sql를 실행하여 데잍베이스에 적용시킨다.(Spring Boot Auto-Config Database h2) <br>
> 만약 Spring Boot의 Default 기본 구성-속성을 쓰고 싶지 않다면, 구성-속성을 변경하여 Custom 해야 한다.

- 스프링부트 자동구성-속성을 사용하여 해당 컴포넌트들의 속성 값을 쉽게 주입할 수 있고 구성에 대해 세부 조정이 쉽게 가능하다.
<hr>

### 5.1.1 스프링 환경 추상화 이해하기
- 스프링 환경 추상화(environment abstraction)는 구성 가능한 모든 속성을 한 곳에서 관리하는 개념이다.
- 스프링 환경에서는 다음과 같은 속성의 근원으로부터 원천 속성을 가져온다.
  > JVM 시스템 속성<br>
  > 운영체제 환경 변수<br>
  > 명령행 인자<br>
  > application.properties, application.yaml<br>

<br>

### #. Example. 애플리케이션을 실행 <br>
&nbsp;&nbsp;&nbsp;&nbsp; 
서블릿 컨테이너가 기본 포트인 8080에서 다른 port 값 설정하기 <br>

1. src/main/resources/application.properties
```properties
server.port=9090
```

2. src/main/resources/application.yaml
```yaml
server:
    port: 9090
```

3. CommandLine Param <br>
Application 시작 시, 지정
```
$ java -jar tacocloud-0.0.5-SNAPSHOT.jar --server.port=9090
```

4. OS 환경변수 설정 <br>
애플리케이션에서 항상 특정 포트를 사용하게 함
```
$ export SERVER_PORT=9090
```
<hr>

### 5.1.2 데이터 소스 구성하기
- 스프링 부트 사용 시에는 Bean을 명시적으로 구성하지 않아도 된다. 
- 구성-속성을 통해 해당 데이터베이스의 URL과 인증을 구성하는 것이 더 간단하다.
- DataSource Bean AutoWiring 후 .yaml 상세 속성들을 이용해 구성한다.
```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost/tacocloud
        username: tacodb
        password: tacopassword
        # 생략 가능
        driver-class-name: com.mysql.jdbc.Driver
```

- 애플리케이션이 시작될 때 데이터베이스를 초기화하는 SQL 스크립트의 실행
- spring.datasource.schema
- spring.datasource.data
```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost/tacocloud
        username: tacodb
        password: tacopassword
        ########### 생략 가능
        driver-class-name: com.mysql.jdbc.Driver
        ###########
        schema:
            - order-schema.sql
            - ingredient-schema.sql
            - taco-schema.sql
            - user-schema.sql
        data:
            - ingredients.sql
```
<hr>

### 5.1.3 내장 서버 구성하기
- server.port가 0으로 설정되면 사용 가능한 포트를 무작위로 선택하여 시작된다.
- 마이크로서비스와 같이 애플리케이션이 시작되는 포트가 중요하지 않을 때 유용하다.
```yaml
server:
    port: 0
```
<br>

- HTTPS 요청 처리를 위한 컨테이너 관련 설정
- keytool - keystore 기반의 인증서와 키를 관리할 수 있는 Tool
- JKS(Java Key Store) - 보안 인증서의 인증서 또는 공개키 인증서 저장소 중의 하나입니다.
- mykeys.jks
```
$keytool -keystore mykeys.jks -genkey -alias tomcat -keyalg RSA
```
```properties
server.port=8443
server.ssl.key-store=/Users/jeongjaegyeop/spring-study/spring-in-action/mykeys.jks
server.ssl.key-store-password=letmein
server.ssl.key-password=letmein
```
<hr>

### 5.1.4 로깅 구성하기
- Logging
- 스프링부트는 기본적으로 INFO 수준으로 콘솔에 로그 메시지를 쓰기 위해 Logback을 통해 로깅을 구성한다.
- https://goddaehee.tistory.com/45
- logback.xml
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>
    <logger name="root" level="INFO" />
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

- Spring Boot는 logback.xml 생성없이 가능하다.
```yaml
logging:
    level:
        root: WARN
        org:
            springframework:
                security: DEBUG

logging:
    # Log File 생성 및 저장
    path: /var/logs/
    file: TacoCloud.log
    level:
        root: WARN
        org:
            springframework:
                security: DEBUG
```
- 스프링 2.0부터는 날짜별로 로그파일이 남으며, 지정된 일 수가 지난 로그 파일은 삭제된다.
<hr>

## 5.2 우리의 구성 속성 생성하기
- 구성 속성의 올바른 주입을 지원하기 위해 스프링 부트는 @ConfigurationProperties 애노테이션을 제공한다.
- 어떤 스프링 빈이건 이 애노테이션을 지정하면, 해당 빈의 속성들이 스프링 환경의 속성으로부터 주입될 수 있다.

> @ConfigurationProperties 동작 예제 <br>
> 고객 주문 리스트 출력 예제, 1 Page 당 20 orders <br>

> first, 구성속성 생성 X
```java
package com.jyjeong.tacos.web;

import ();

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

    private OrderRepository orderRepo;

    @Autowired
    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    //.......

    @GetMapping
    public String ordersForUser(@AuthenticationPrincipal User user, Model model) {

        Pageable pageable = PageRequest.of(0,20);
        model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));
        return "orderList";
    }
}
```
```java
package com.jyjeong.tacos.data;

import ();

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);
    //Order save(Order order);
}
```
- 수정 시, 로직 내 하드코딩을 수정해야한다. 유지보수 불편.<br><br>

> second, 구성속성 추가
- @ConfigurationProperties 사용
- prefix = taco.orders 이며, pageSize는 taco.orders.pageSize라는 이름으로 사용된다.
```java
package com.jyjeong.tacos.web;

import ();

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
@ConfigurationProperties(prefix = "taco.orders")
public class OrderController {

    private OrderRepository orderRepo;

    @Autowired
    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    private int pageSize = 20;
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    @GetMapping
    public String ordersForUser(@AuthenticationPrincipal User user, Model model) {

        Pageable pageable = PageRequest.of(0,20);
        model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageSize));
        return "orderList";
    }
}
```
```yaml
taco:
    orders:
        pageSize: 10
```
- yaml로 속성 값들이 관리되면, 유지보수에 쉽다.
<hr>

### 5.2.1 구성 속성 홀더 정의하기
- 구성-속성 값이 Controller에 존재하는 것이 불편하다.
- 결국 모든 Class에 존재할 수 있으며, 이는 아무리 변수로 관리한다고 하더라도 관리가 어렵다.
- 구성 속성 홀더 클래스를 정의하여 컨트롤러와 이외의 다른 애플리케이션 클래스 외부에 구성 관련 정보를 따로 유지하자는 개념
- 여러 빈에 공통적인 구성 속성을 쉽게 공유할 수 있다.
```java
package com.jyjeong.tacos.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Component
@ConfigurationProperties(prefix = "taco.orders")
@Data
@Validated
public class OrderProps {

    @Min(value=5, message = "must be between 5 and 25")
    @Max(value=25, message = "must be between 5 and 25")
    private int pageSize = 20;
}
```
- 구성-속성 값들에 대한 Validation 체크도 로직 내부에 흩어진 형태가 아닌 외부에서 가능하므로 효율적이다.
<hr>

### 5.2.2 구성 속성 메타데이터 선언하기
- 스프링 Project 내부에 meta data를 선언하여 애플리케이션이 이를 인지할 수 있도록 정보를 제공함.
- 안써도 그만임
- 솔직히, 장점이 자동완성 밖에 없는 듯 하지만. 자동으로 메타데이터를 생성해주는 library가 존재하므로 dependency만 소개함.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>
        spring-boot-configuration
    </artifactId>
    <optional>true</optional>
</dependency>
```
<hr>

## 5.3 프로파일 사용해서 구성하기
- 개발환경, 운영환경의 구성-설정이 달라진다. <br>
  Ex) DEV, PROD DEV 설정
- 런타임 환경에 따라 설정을 다르게 해줘야 한다.

> 운영체제의 환경 변수를 사용
```js
// 환경 변수를 매번 쓰는 것은 매우 비효율적이기에 좋지 않은 방법이다.
% export SPRING_DATASOURCE_URL=jdbc:mysql://localhost/tacocloud
% export SPRING_DATASOURCE_USERNAME=tacouser
% export SPRING_DATASOURCE_PASSWORD=tacopassword
```
<br>

## 5.3.1 프로파일 특정 속성 정의하기
- 런타임 시에 활성화 되는 프로파일에 따라 서로 다른 빈, 구성 클래스, 구성 속성들이 적용 또는 무시되도록 하는 것이 프로파일이다.
<br>

> 구성파일을 환경에 따라 정의함.
- application-[profile name].yml
- application-[profile name].properties

> .yaml 프로파일에 특정되지 않고 공통으로 적용되는 기본 속성과 함께 프로파일 특정 속성을 지정한다.
- application.yaml
- (---)로 구분하며, profiles가 정의되어있지 않은 부분은 공통, profiles가 정의된 부분은 지정이다.
```yaml
logging:
    level:
        tacos: DEBUG

---
spring:
    profiles: prod
    datasource:
        url: jdbc:mysql://localhost/tacocloud
        username: tacouser
        password: tacopassword

logging:
    level:
    tacos: WARN
```
<hr>

## 5.3.2 프로파일 활성화하기
- 프로파일 특정 속성들의 설정은 해당 프로파일이 활성화 되어야 유효하다.
- 활성화 시키는 방법

> application.yaml에 active / 좋지 않은 방법(최악)
```yaml
spring:
    profiles:
        active:
        - prod
```

> 환경 변수를 사용해서 활성화 프로파일을 설정 / 좋은 방법
```js
% export SPRING_PROFILES_ACTIVE=prod
```

> JAR 파일로 애플리케이션이 실행될 때, 명령행 인자로 활성화 프로파일 결정 / 괜찮
```js
% java -jar taco-cloud.jar --spring.profiles.active=prod
```
<hr>

## 5.3.3 프로파일을 사용해서 조건별로 빈 생성하기
- 특정 프로파일이 활성화될 때만 생성되어야 하는 빈들이 있을 때 사용
- @Profile 애노테이션을 사용하면 지정된 프로파일에만 적합한 빈들을 나타낼 수 있다.
```java
package com.jyjeong.tacos;

import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class TacoCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(TacoCloudApplication.class, args);
    }

    @Bean
    @Profile("dev"), @Profile({"dev","qa"}), @Profile("!prod")
    // 지정 profile에만 Bean으로 등록하는 개념
    public CommandLineRunner dataLoader(IngredientRepository repo) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                //...
            }
        };
    }

}
```
<br>

- 만약 dev 환경에서 Bean 등록이 많이 이루어진다면, <br>
(다른 profile도 동일하게 지정)
- Project 내에서는 하나의 Config.class로 모아서 관리하면 된다.
```java
@Profile("!prod")
@Configuration
public class DevelopmentConfig {
    @Bean
    public CommandLineRunner dataLoader(IngredientRepository repo, UserRepository userRepo. PasswordEncoder encoder) {
        //........
    }

}
```