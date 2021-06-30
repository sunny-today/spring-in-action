package tacos.data;

import org.springframework.data.repository.CrudRepository;
import tacos.Ingredient;

//// JDBC
//public interface IngredientRepository {
//	Iterable<Ingredient> findAll();
//	Ingredient findById(String id);
//	Ingredient save(Ingredient ingredient);
//}


public interface IngredientRepository extends CrudRepository<Ingredient, String> {
	
}