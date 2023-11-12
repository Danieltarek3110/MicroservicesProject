package com.example.microservices.orderservice.Repository;

import com.example.microservices.orderservice.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {


}
