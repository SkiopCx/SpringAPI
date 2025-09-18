package com.codewithmosh.store.orders;

import com.codewithmosh.store.carts.Cart;
import com.codewithmosh.store.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders", schema = "store_api")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

    public static Order fromCart(Cart cart, User user) {
        Order order = new Order();
        order.setCustomer(user);
        order.setTotalPrice(cart.getTotal());
        order.setStatus(OrderStatus.PENDING);

        cart.getCartItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem(order, cartItem.getProduct(), cartItem.getQuantity());
            order.orderItems.add(orderItem);
        });
        return order;
    }

    public boolean isPlacedBy(User user) {
        return customer.equals(user);
    }
}