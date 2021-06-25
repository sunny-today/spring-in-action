package com.ssookie.tacos.web;

import com.ssookie.tacos.Ingredient;
import com.ssookie.tacos.Order;
import com.ssookie.tacos.Taco;
import com.ssookie.tacos.data.IngredientRepository;
import com.ssookie.tacos.data.TacoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;
    private TacoRepository tacoRepo;

    @Autowired // IngredientRepository 를 DesignTacoController에 주입(연결)
    public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepo) {
        this.ingredientRepo = ingredientRepo;
        this.tacoRepo = tacoRepo;
    }


    @GetMapping
    public String showDesignForm(Model model) {

        // 모든 식자재 데이터를 가져옴
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepo.findAll().forEach(i -> ingredients.add(i));

        // 타입별로 식자재 필터링
        Ingredient.Type[] types = Ingredient.Type.values();
        for(Ingredient.Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }

        model.addAttribute("taco", new Taco());

        return "design";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Ingredient.Type type) {
        return ingredients.stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    /**
     * 타코 디자인을 실제로 처리(저장)
     * @param design
     * @param errors
     * @param order
     * @return
     */
    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {
        // @ModelAttribute Order -  이 매개변수 값이 모델로부터 전달되어야 함
        // (=스프링  MVC가 이 매개변수에 요청 매개변수를 바인딩하지 않아야 함.)

        if(errors.hasErrors()) {
            return "design";
        }

        log.info("Processing design: " + design);
        Taco saved = tacoRepo.save(design); // 타코 저장
        order.addDesign(saved); // 세션에 보존된 order에 Taco 객체를 전달, order 객체는 session 에 남아 있음.

        return "redirect:/orders/current";
    }
}
