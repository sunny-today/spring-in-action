package com.skyshop300.tacos.data;

import org.springframework.data.repository.CrudRepository;

import com.skyshop300.tacos.Taco;

public interface TacoRepository extends CrudRepository<Taco, Long> {
	
}