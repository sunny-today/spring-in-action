package com.ssookie.tacos.web;

import com.ssookie.tacos.Order;
import com.ssookie.tacos.data.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order") // order 객체를 세션에 보존
public class OrderController {

    private OrderRepository orderRepo;

    public OrderController(OrderRepository orderRepo){
        this.orderRepo = orderRepo;
    }

    @GetMapping("/current")
    public String orderForm(Model model) {
        // model.addAttribute("order", new Order());
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus) {
        if(errors.hasErrors()) {
            return "orderForm";
        }

        log.info("Order submitted: " + order);

        orderRepo.save(order);
        sessionStatus.setComplete();    // 데이터 저장 후에는, 더이상 세션에 보존할 필요가 없음

        return "redirect:/";
    }
}
