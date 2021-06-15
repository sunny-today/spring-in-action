## @SpringBootApplication
- https://bamdule.tistory.com/31
```java
package com.jyjeong.tacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TacoCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(TacoCloudApplication.class, args);
    }
}
```
```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.springframework.boot.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
    @AliasFor(
        annotation = EnableAutoConfiguration.class
    )
    Class<?>[] exclude() default {};

    @AliasFor(
        annotation = EnableAutoConfiguration.class
    )
    String[] excludeName() default {};

    @AliasFor(
        annotation = ComponentScan.class,
        attribute = "basePackages"
    )
    String[] scanBasePackages() default {};

    @AliasFor(
        annotation = ComponentScan.class,
        attribute = "basePackageClasses"
    )
    Class<?>[] scanBasePackageClasses() default {};

    @AliasFor(
        annotation = Configuration.class
    )
    boolean proxyBeanMethods() default true;
}

```
- @ComponentScan <br>
  @ComponentScan은 @component 어노테이션 및 @Service, @Repository, @Controller 등의 어노테이션을 스캔하여 Bean으로 등록해주는 어노테이션

- @EnableAutoConfiguration <br>
  사전에 정의한 라이브러리들을  Bean으로 등록해 주는 어노테이션입니다. 
  사전에 정의한 라이브러리들 모두가 등록되는 것은 아니고 특정 Condition(조건)이 만족될 경우에 Bean으로 등록합니다.
  - Dependencies > spring-boot-autoconfigure > META-INF > spring.factories
<hr>


## Java Annotation Processor
- javax.annotation.processing <br>
  https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/Processor.html

- 컴파일 단계에서 애노테이션에 정의된 액션을 처리
- 컴파일 단계에서 Annotation을 체킹하여 파일(.java, .class) "생성"
- AbstractProcessor를 상속받아 구현
```java
public @interface myAnnotation{}
public class myAnnotationProcessor extends AbstractProcessor{}
public abstract class AbstractProcessor implements Processor{}
public interface Processor{}
```
- Example 설명. @Magic

## Lombok Issue
- Lombok도 Annotation을 사용하여 구현
- 다양한 기능을 제공하여 너무 편리하다.
- getter, setter, Logger, 생성자 등등

> ### #. Lombok은 해킹이다? <br>
> 기존 java annotation processor를 사용한다. 하지만, Lombok은 단순히 "생성"이 아니라 변형한다. <br>
> 공개된 API가 아닌 컴파일러 내부 클래스를 사용하여 컴파일러 버전이 변경되면 소스코드가 돌아가지 않을수도 있다. <br>
> Example) Order.java <br>


## Annotatio Def, Custom
- @Configuration <br>
  SingleTon 유지 / @Bean Annotation이랑 비교 예제 보기 <br>
  https://castleone.tistory.com/2

## Bean Validation
- Java에서는 2009년부터 Bean Validation이라는 데이터 유효성 검사 프레임워크를 제공
- Bean Validation은 위에서 말한 문제들을 해결하기 위해 다양한 제약(Contraint)을 도메인 모델(Domain Model)에 어노테이션(Annotation)로 정의
- 이 제약을 유효성 검사가 필요한 객체에 직접 정의하는 방법으로 기존 유효성 검사 로직의 문제를 해결

> Tech. <br>
-. Bean Validation 2.0 <br>
-. Hibernate  Validator 6.0 <br>
-. Spring Boot 2.0 <br>

- NHN Toast / https://meetup.toast.com/posts/223 <br>
  NS 내 두레이(nhn toast service)도 이것으로 Validation Checking

