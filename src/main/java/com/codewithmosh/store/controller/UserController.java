package com.codewithmosh.store.controller;

import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.RegisterUserRequest;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping
    public Iterable<UserDto> getAllUser(
            @RequestHeader(name = "x-auth-token", required = false) String token,
            @RequestParam(name = "sort", defaultValue = "", required = false) String sort) {
        if (!Set.of("name", "email").contains(sort)) {
            sort = "name";
        }

        System.out.println("token: " + token);

        List<User> users = userRepository.findAll(Sort.by(sort));
        return users.stream().map(u -> userMapper.toUserDto(u)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toUserDto(user.get()));
    }

    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder) {
        boolean isExistsEmail = userRepository.existsByEmail(request.getEmail());
        if (isExistsEmail) {
            return ResponseEntity.badRequest().body(Map.of("Email", "Email is exists already"));
        }

        User user = userMapper.toUser(request);
        userRepository.save(user);

        UserDto userDto = userMapper.toUserDto(user);
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserRequest request, @PathVariable(name = "id") Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.updateUser(request, user);
        userRepository.save(user);

        UserDto userDto = userMapper.toUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable(name = "id") Long id, @RequestBody ChangePasswordRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!request.getOldPassword().equals(user.getPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}
