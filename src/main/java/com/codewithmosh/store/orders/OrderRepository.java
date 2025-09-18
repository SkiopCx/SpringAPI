package com.codewithmosh.store.orders;

import com.codewithmosh.store.users.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.customer = :user")
    @EntityGraph(attributePaths = "orderItems.product")
    List<Order> getAllByCustomer(@Param("user") User user);

    @EntityGraph(attributePaths = "orderItems.product")
    Optional<Order> findWithItemsById(Long orderId);
}