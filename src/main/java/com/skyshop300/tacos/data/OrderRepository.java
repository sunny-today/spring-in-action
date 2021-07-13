package com.skyshop300.tacos.data;

import org.springframework.data.repository.CrudRepository;

import com.skyshop300.tacos.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

}