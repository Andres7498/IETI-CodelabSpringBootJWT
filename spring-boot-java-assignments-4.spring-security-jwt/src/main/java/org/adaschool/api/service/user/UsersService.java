package org.adaschool.api.service.user;

import org.adaschool.api.repository.user.User;
import org.adaschool.api.repository.user.UserDto;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    User save(UserDto userDto);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    List<User> all();

    void deleteById(String id);

    User update(UserDto userDto, String userId);
}

