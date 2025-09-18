package com.codewithmosh.store.orders;

import com.codewithmosh.store.auth.AuthService;
import com.codewithmosh.store.users.User;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderService {

    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> findAllByCustomer() {
        User user = authService.getCurrentUser();
        List<Order> orders = orderRepository.getAllByCustomer(user);
        return orders.stream().map(orderMapper::toOrderDto).toList();
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findWithItemsById(id).orElseThrow(OrderNotFoundException::new);

        User currentUser = authService.getCurrentUser();
        if (!order.isPlacedBy(currentUser)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        return orderMapper.toOrderDto(order);
    }
}
