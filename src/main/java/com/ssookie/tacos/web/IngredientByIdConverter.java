package com.ssookie.tacos.web;

import com.ssookie.tacos.Ingredient;
import com.ssookie.tacos.data.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IngredientByIdConverter implements Converter<String, Ingredient> {
    private IngredientRepository ingredientRepo;

    @Autowired
    public IngredientByIdConverter(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    /**
     * 스프링의 Converter 인터페이스에 정의된 convert()구현 → 타입 변환시 convert() 메서드 자동 호출
     * @param id
     * @return
     */
    @Override
    public Ingredient convert(String id) {
        // JPA
        Optional<Ingredient> optionalIngredient = ingredientRepo.findById(id);
        return optionalIngredient.isPresent() ? optionalIngredient.get() : null;
        // jdbc
//        return ingredientRepo.findById(id);
    }
}
