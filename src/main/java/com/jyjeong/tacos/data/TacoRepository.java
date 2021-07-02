package com.jyjeong.tacos.data;

import com.jyjeong.tacos.Taco;
import org.springframework.data.repository.CrudRepository;

public interface TacoRepository extends CrudRepository<Taco, Long> {
    //Taco save(Taco design);
}
