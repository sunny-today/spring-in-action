## CHAPTRER 2. 웹 애플리케이션 개발하기
<hr>
#. Chapter2's Goal<br>
1. Model Data를 Browser에 보여주기<br>
2. Form 입력 처리하고 검사하기<br>
3. 뷰 템플릿 라이브러리 선택하기<br>
<hr>

## 2.1 정보 보여주기
- Taco Cloud Application
- 온라인으로 타코를 주문할 수 있는 애플리케이션을 실습하고 이를 통해 설명한다.

### 2.1.1 도메인 설정하기
- 온라인으로 타코를 주문하기 위한 Application의 도메인을 파악한다.
- Domain Object
  > 고객이 선택한 타코 디자인<br>
  > 디자인을 구성하는 식자재<br>
  > 고객<br>
  > 고객의 타코 주문<br>

<hr>

- 디자인을 구성하는 식자재 정의

```java
package com.jyjeong.tacos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Ingredient {
    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
```
- lombok. java AnnotationProcessor
- <span style="color:yellow">lombok Issue</span>
<hr>

- 고객이 선택한 타코 디자인
```java
package com.jyjeong.tacos;

import lombok.Data;
import java.util.List;

@Data
public class Taco {

    private String name;
    private List<String> ingredients;
}
```
- <span style="color:yellow">Lombok @Data 를 이용하여 생성자/getter/setter 등은 언제/어떻게 생성되는 것일까?</span>
<hr>

### 2.1.2 컨트롤러 클래스 생성하기
- 컨트롤러는 HTTP 요청을 처리
- 컨트롤러는 브라우저에 보여줄 HTML을 뷰에 요청하거나(2장)
- REST 형태의 응답 몸체에 직접 데이터를 추가한다.(6장)
  >-. 요청 경로가 /design인 HTTP GET 요청을 처리한다.<br>
  >-. 식자재의 내역을 생성한다.<br>
  >-. 식자재 데이터의 HTML 작성을 뷰 템플릿에 요청하고, 작성된 HTML을 웹 브라우저에 전송한다.<br>

- HTTP 요청 Controller
<hr>

- 고객이 선택한 타코 디자인
```java
package com.jyjeong.tacos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {

    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = Arrays.asList(
                new Ingredient("FLTO", "Flour Torilla", Ingredient.Type.WRAP),
                new Ingredient("COTO", "Corn Tortilla", Ingredient.Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
                new Ingredient("CARN", "Carnitas", Ingredient.Type.PROTEIN),
                new Ingredient("TMTO", "Diced Tomatoes", Ingredient.Type.VEGGIES),
                new Ingredient("LETC", "Lettuce", Ingredient.Type.VEGGIES),
                new Ingredient("CHED", "Cheddar", Ingredient.Type.CHEESE),
                new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE),
                new Ingredient("SLSA", "Salsa", Ingredient.Type.SAUCE),
                new Ingredient("SRCR", "Sour Cream", Ingredient.Type.SAUCE)
                );

        Ingredient.Type[] types = Ingredient.Type.values();
        for(Ingredient.Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }

        model.addAttribute("taco", new Taco());

        return "design";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Ingredient.Type type) {
        return ingredients.stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }
}
```
- @Slf4j Annotation : Lombok, DesignTacoController.class Checking !
```java
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DesignTacoController.class)
```
- @Controller Annotation
- @RequestMapping Annotation - url Path 및 value를 지정하여 HTTP 요청을 처리함.
- @GetMapping - Spring 4.3에서 소개<br>
  @RequestMapping(method=RequestMethod.GET)과 동일함.<br>
  이전보다 가독성이 뛰어남. HTTP Method별로 모두 존재함<br>
- 필자의 경우 클래스 수준의 Path 결정 시 @RequestMapping, Method 별 Path 결정 시 @GetMapping, @PostMapping 등을 이용한다.
- org.springframework.ui.Model<br>
  https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/ui/Model.html<br>
  ConcurrentHashMap Class. map 형태(key,value)<br>
  @ModelAttribute 를 Method에 지정하면 리턴된 객체도 View에 전달가능하다.<br>
  >-. Model은 컨트롤러와 데이터를 보여주는 뷰 사이에서 데이터를 운반하는 객체이다.<br>
  >-. Model 객체의 속성에 있는 데이터는 뷰가 알 수 있는 서블릿 요청 속성들로 복사된다.<br>
<hr>


## 2.2 폼 제출 처리하기   
- <form> 태그에 action 속성을 선언하지 않으면 GET 요청과 같은 경로로 서버에 HTTP POST 요청을 전송한다.
- <form> 태그에 action 속성은 해당 경로로 HTTP POST 요청을 전송한다는 의미이다.
- @PostMapping Annotation Use
- processDesign은 고객이 선택한 타코디자인을 받아서 처리하는 부분이다.
```java
@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {

    @GetMapping
    public String showDesignForm(Model model) {...}

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors) {

        if(errors.hasErrors()) {
            return "design";
        }

        // 타코 디자인(선택된 식자재 내역)을 저장
        // 3장
        log.info("Processing design: " + design);

        return "redirect:/orders/current";
    }
   
}
```
- form으로 제출된 Taco 객체를 데이터베이스에 저장하는 퍼시스턴스 로직은 3장에서 추가
- return "redirect:/orders/current" /orders/current 경로로 재접속 되어야 한다는 것을 의미한다.
<hr>

- 타코 주문 폼을 나타내는 컨트롤러
- 3장에서는 모델 데이터를 데이터베이스에 저장할 때 주문된 Taco 객체들로 모델을 채우도록 이 메서드를 변경
```java
package com.jyjeong.tacos.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.jyjeong.tacos.Order;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/current")
    public String orderForm(Model model) {
        model.addAttribute("order", new Order());
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid Order order, Errors errors) {
        if(errors.hasErrors()) {
            return "orderForm";
        }

        log.info("Order submitted: " + order);
        return "redirect:/";
    }
}
```

- 타코 주문 정보를 갖는 도메인 객체
```java
package com.jyjeong.tacos;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class Order {

    private String deliveryName;
    private String deliveryStreet;
    private String deliveryCity;
    private String deliveryState;
    private String ccNumber;
    private String ccExpiration;
    private String ccCVV;
}
```
<hr>
# GET : /design <br>
# POST : /design <br>
# GET : /orders/current <br>
# POST : /orders <br>
# GET : / <br>
<hr>

## 2.3 폼 입력 유효성 검사하기
- 스프링은 자바의 빈 유효성 검사 API를 지원한다.
- 애플리케이션에 추가 코드를 작성하지 않고 유효성 검사 규칙을 쉽게 선언.
- 유효성 검사 API, 구현체(Hibernate) Component
- <span style="color:yellow">Bean Validation Framework, Hibernate</span>

### 2.3.1 유효성 검사 규칙 선언하기
- Null Check / @NotNull
- Size Check / @Size
- Validation Check Fail -> print message
- Taco.java
```java
package com.jyjeong.tacos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class Taco {

    @NotNull
    @Size(min = 5, message = "Name must be at least 5 characters long")
    private String name;

    @Size(min = 1, message = "You must choose at least 1 ingredient")
    private List<String> ingredients;
}

```

- Blank Check(null,""," ") / @NotBlank
- CreditCardNumber(신용카드번호 양식) / @CreditCardNumber
- Pattern(정규 표현식) / @Pattern
- Digits(속성, 값) / @Digits
```java
package com.jyjeong.tacos;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class Order {

    @NotBlank(message = "Name is required")
    private String deliveryName;

    @NotBlank(message = "Name is required")
    private String deliveryStreet;

    @NotBlank(message = "Name is required")
    private String deliveryCity;

    @NotBlank(message = "Name is required")
    private String deliveryState;

    @NotBlank(message = "Name is required")
    private String deliveryZip;

    @CreditCardNumber(message = "Not a valid credit card number")
    private String ccNumber;

    @Pattern(regexp = "^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
             message = "Must be formatted MM/YY")
    private String ccExpiration;

    @Digits(integer = 3, fraction = 0, message = "Invalid CVV")
    private String ccCVV;
}
```

### 2.3.2 폼과 바인딩될 때 유효성 검사 수행하기
- 각 폼의 POST 요청이 관련 메서드에서 처리될 때 유효성 검사가 수행되도록 컨트롤러를 변경
- 메서드 인자로 전달되는 인자 값에 @Valid Annotation 추가
```java

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors) {...}
}
```
- Taco 객체에 정의된 유효성 검사를 수행한다.
- 만일 어떤 검사 에러라도 있으면 에러의 상세 내역이 Errors 객체에 저장되어 processDesign()으로 전달된다.
- Dynamic Proxy 개념인가 ?
<hr>

### 2.3.3 유효성 검사 에러 보여주기
- View 영역에서 유효성 검사 Error Message 출력
- 읽어보세요.

## 2.4 뷰 컨트롤러로 작업하기
>- HomeController / DesignTacoController / OrderController<br>
  컨트롤러 클래스임을 나타내기 위해 모두 @Controller Annotation을 사용
>- HomeController는 자신이 처리하는 요청 패턴을 정의하기 위해 @RequestMapping Annotation을 사용
>- @GetMapping, @PostMapping

<br>

- 모델 데이터나 사용자 입력을 처리하지 않는 간단한 컨트롤러의 경우는 뷰에 요청을 전달하는 일만 하는 컨트롤러(뷰 컨트롤러)를 선언할 수 있다.
- View Controller
```java
package com.jyjeong.tacos.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
```
- <span style="color:yellow">@Configuration</span>
- WebConfig는 뷰 컨트롤의 역할을 수행하는 구성 클래스이며, WebMvcConfigurer 인터페이스를 구현한다는 것
- WebMvcConfigurer 인터페이스는 스프링 MVC를 구성하는 메서드를 정의
- addViewControllers() 오버라이딩 <br>
  하나 이상의 뷰 컨트롤러를 등록하기 위해 사용할 수 있는 ViewControllerRegistry를 인자로 받는다.
- Model(Object)로 넘어가면 ViewResolver 개념 알아야함!
<hr>