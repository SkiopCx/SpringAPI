package com.codewithmosh.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts", schema = "store_api")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    public BigDecimal getSum(){
        BigDecimal sum = BigDecimal.ZERO;
        return cartItems.stream().map(item -> item.getTotalPrice()).reduce(sum, BigDecimal::add);
    }

    public CartItem findCartItemById(Long productId){
        return this.getCartItems().stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst().orElse(null);
    }

    public CartItem addCartItem(Product product){
        CartItem cartItem = findCartItemById(product.getId());
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(this);
            cartItems.add(cartItem);
        }
        return cartItem;
    }

    public void removeCartItem(Long productId){
        CartItem cartItem = findCartItemById(productId);
        if (cartItem != null) {
            cartItems.remove(cartItem);
            cartItem.setCart(null);
        }
    }

    public void clearCartItems(){
        cartItems.clear();
    }
}