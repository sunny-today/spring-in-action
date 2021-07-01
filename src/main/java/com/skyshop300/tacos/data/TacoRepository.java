package tacos.data;

import tacos.Taco;
import org.springframework.data.repository.CrudRepository;

//// JDBC
//public interface TacoRepository {
//	Taco save(Taco design);
//}



public interface TacoRepository extends CrudRepository<Taco, Long> {
	
}