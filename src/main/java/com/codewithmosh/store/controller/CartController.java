package com.codewithmosh.store.controller;

import com.codewithmosh.store.dtos.AddItemToCartRequest;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartItemRequest;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
@AllArgsConstructor
@Tag(name = "Carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> creatCart(UriComponentsBuilder uriBuilder) {
        CartDto cartDto = cartService.creatNewCart();

        URI uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    @Operation(summary = "Add a product")
    public ResponseEntity<CartItemDto> addItemToCart(
            @Parameter(description = "The ID of the cart") @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequest request) {
        CartItemDto cartItemDto = cartService.addItem(cartId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable UUID cartId) {
        CartDto cartDto = cartService.getCart(cartId);
        return cartDto;
    }

    @PutMapping("/{cartId}/items/{productId}")
    public CartItemDto updateCartItem(@PathVariable("cartId") UUID cartId, @PathVariable("productId") Long productId, @Valid @RequestBody UpdateCartItemRequest request){
        CartItemDto cartItemDto = cartService.modifyCartItem(cartId, productId, request.getQuantity());
        return cartItemDto;
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("cartId") UUID cartId, @PathVariable("productId") Long productId) {
        cartService.removeCartItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(@PathVariable("cartId") UUID cartId){
        cartService.clearItem(cartId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Cart not found")
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "Product not found")
        );
    }

}
