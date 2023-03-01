package org.adaschool.api.controller.user;

import org.adaschool.api.exception.UserNotFoundException;
import org.adaschool.api.repository.user.User;
import org.adaschool.api.repository.user.UserDto;
import org.adaschool.api.service.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.adaschool.api.utils.Constants.ADMIN_ROLE;

@RestController
@RequestMapping("/v1/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        var savedUser = usersService.save(userDto);
        URI createdUserUri = URI.create("/v1/users/" + savedUser.getId());
        return ResponseEntity.created(createdUserUri).body(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = usersService.all();
        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> findById(@PathVariable("id") String id) {
        Optional<User> optionalUser = usersService.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        return ResponseEntity.ok(optionalUser.get());
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody UserDto userDto) {
        User updatedUser = usersService.update(userDto, id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("{id}")
    @RolesAllowed(ADMIN_ROLE)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        Optional<User> existingUser = usersService.findById(id);
        if (existingUser.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        usersService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
