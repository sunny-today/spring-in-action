package com.ssookie.tacos.data;
import com.ssookie.tacos.Ingredient;
import org.springframework.data.repository.CrudRepository;

/**
 * IngredientrRepository가 해야할 연산을 정의
 */
public interface IngredientRepository extends CrudRepository<Ingredient, String> {
    /**
     * JDBC 사용
     */
//    Iterable<Ingredient> findAll();
//    Ingredient findById(String id);
//    Ingredient findByIdMap(String id);
//    Ingredient save(Ingredient ingredient);
}
