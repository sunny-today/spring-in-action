package com.ssookie.tacos.data;

import com.ssookie.tacos.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
    /**
     * JDBC 사용
     */
//    Order save(Order order);
}
