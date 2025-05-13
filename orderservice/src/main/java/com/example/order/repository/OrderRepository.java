package com.example.order.repository;

import com.example.order.model.Order;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface OrderRepository extends CrudRepository<Order, Long> {
}
