```java
```
## Exception
- Error Vs. Exception
  Error : 시스템에 비정상적인 상황이 생겼을 때 발생한다. 시스템 레벨에서 발생하기 때문에 심각한 수준의 오류이다.
          개발자가 미리 예측할 수 없다.
  Exception : 개발자가 구현한 로직에서 발생한다. 예외는 발생할 상황을 미리 예측하여 처리할 수 있다.
              예외는 개발자가 처리할 수 있기 때문에 예외를 구분하고 그에 따른 처리 방법을 명확히 알고 구분해야 한다.

> Checked Exception Vs. Unchecked Exception <br>
> Checked Exception은 Compile 시점 - rollback 하지 않으며 RuntimeException을 상속받지 않는 모든 Exception <br>
> Unchecked Exception은 Runtime 시점 - rollback 하며 RuntimeException을 상속받는 모든 Exception <br>
> Checked Exception은 필수로 예외처리를 해야하지만 Unchecked Exception은 필수로 예외처리 하지 않아도 되지만 하는 것이 옳다. <br>
> 예외처리 시 Throw로 던지지 말고 try-catch로 처리하는 것이 좋다. <br>

## JdbcTemplate
- query
- queryForObject
- RowMapper Interface
- mapRow method implement
- 어떻게 돌아가는지 Step 까보기

## @Autowired
- Dependency Injection Study

## PreparedStatementCreator, KeyHolder

## @SessionAttributes("order"), SessionStatus

## @ModelAttribute(name = "order")

## SimpleJdbcInsert, ObjectMapper


## Converter ?
package com.jyjeong.tacos.web;

import com.jyjeong.tacos.Ingredient;
import com.jyjeong.tacos.data.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IngredientByIdConverter implements Converter<String, Ingredient> {

    private IngredientRepository ingredientRepo;

    @Autowired
    public IngredientByIdConverter(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @Override
    public Ingredient convert(String id) {
        return ingredientRepo.findById(id);
    }
}





















