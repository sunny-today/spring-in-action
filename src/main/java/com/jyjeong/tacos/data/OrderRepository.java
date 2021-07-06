package com.jyjeong.tacos.data;

import com.jyjeong.tacos.Order;
import com.jyjeong.tacos.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);
    //Order save(Order order);
}
