package com.skyshop300.tacos.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.skyshop300.tacos.Ingredient;
import com.skyshop300.tacos.data.IngredientRepository;

@Component
public class IngredientByIdConverter implements Converter<String, Ingredient> {
	private IngredientRepository ingredientRepo;
	
	@Autowired
	public IngredientByIdConverter(IngredientRepository ingredientRepo) {
		this.ingredientRepo = ingredientRepo;
	}
	
	@Override
	public Ingredient convert(String id) {
//		return ingredientRepo.findById(id);
		Optional<Ingredient> optionalIngredient = ingredientRepo.findById(id);
		return optionalIngredient.isPresent() ? optionalIngredient.get() : null;
		
	}
}