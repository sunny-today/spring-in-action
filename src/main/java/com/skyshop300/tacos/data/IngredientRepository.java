package com.skyshop300.tacos.data;

import org.springframework.data.repository.CrudRepository;

import com.skyshop300.tacos.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {
	
}