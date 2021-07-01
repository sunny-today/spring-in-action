package tacos.web;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import lombok.extern.slf4j.Slf4j;
import tacos.Order;
import tacos.User;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {
	
	private OrderRepository orderRepo;
	private UserRepository userRepo;
	
	public OrderController(OrderRepository orderRepo) {
		this.orderRepo = orderRepo;
	}
	
	@GetMapping("/current")
	public String orderForm(@AuthenticationPrincipal User user, @ModelAttribute Order order) { // 인증된 사용자를 메서드 인자로 받아서 해당 사용자의 이름과 주소를 order 객체의 각 속성에 설정한다.
		if (order.getDeliveryName() == null) {
			order.setDeliveryName(user.getFullname());
		}
		if (order.getDeliveryStreet() == null) {
			order.setDeliveryStreet(user.getStreet());
		}
		if (order.getDeliveryCity() == null) {
			order.setDeliveryCity(user.getCity());
		}
		if (order.getDeliveryState() == null) {
			order.setDeliveryState(user.getState());
		}
		if (order.getDeliveryZip() == null) {
			order.setDeliveryZip(user.getZip());
		}
		return "orderForm";
	}
	
	/*
	 * 방법3)  processOrder()의 인자로 User 객체를 전달
	 */
	@PostMapping
	public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
		if (errors.hasErrors()) {
			return "orderForm";
		}
		
		order.setUser(user);
		
		orderRepo.save(order);
		sessionStatus.setComplete();
		
		return "redirect:/";
	}
	
	/*
	 * 방법1) Principal 객체를 Controller 메서드에 주입
	 */
//	@PostMapping
//	public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Principal principal) {
//		if (errors.hasErrors()) {
//			return "orderForm";
//		}
//		
//		User user = userRepo.findByUsername(principal.getName());
//		order.setUser(user);
//		
//		orderRepo.save(order);
//		sessionStatus.setComplete();
//		
//		return "redirect:/";
//	}
	
	/*
	 * 방법2) Authentication 객체를 인자로 받도록 processOrder() 메서드 변경
	 */
//	@PostMapping
//	public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Authentication authentication) {
//		if (errors.hasErrors()) {
//			return "orderForm";
//		}
//		
//		User user = (User) authentication.getPrincipal();
//		order.setUser(user);
//		
//		orderRepo.save(order);
//		sessionStatus.setComplete();
//		
//		return "redirect:/";
//	}
	
	/*
	 * 방법4) 
	 */
//	@PostMapping
//	public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus) {
//		if (errors.hasErrors()) {
//			return "orderForm";
//		}
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		User user = (User) authentication.getPrincipal();
//		order.setUser(user);
//		
//		orderRepo.save(order);
//		sessionStatus.setComplete();
//		
//		return "redirect:/";
//	}
	
}