package com.jyjeong.tacos.data;

import com.jyjeong.tacos.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
    //Order save(Order order);
}
