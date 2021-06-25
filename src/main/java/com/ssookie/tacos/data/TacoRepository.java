package com.ssookie.tacos.data;

import com.ssookie.tacos.Taco;
import org.springframework.data.repository.CrudRepository;

public interface TacoRepository extends CrudRepository<Taco, Long> {
    /**
     * JDBC 사용
     */
//    Taco save(Taco design);
}
