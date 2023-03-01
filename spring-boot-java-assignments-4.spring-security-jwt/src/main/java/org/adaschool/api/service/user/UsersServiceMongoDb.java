package org.adaschool.api.service.user;

import org.adaschool.api.exception.UserNotFoundException;
import org.adaschool.api.repository.user.User;
import org.adaschool.api.repository.user.UserDto;
import org.adaschool.api.repository.user.UserRepository;
import org.adaschool.api.security.encrypt.PasswordEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersServiceMongoDb implements UsersService {

    private final UserRepository userRepository;

    private final PasswordEncryptionService passwordEncryptionService;

    @Autowired
    public UsersServiceMongoDb(@Autowired UserRepository userRepository, @Autowired PasswordEncryptionService passwordEncryptionService) {
        this.userRepository = userRepository;
        this.passwordEncryptionService = passwordEncryptionService;
    }

    @Override
    public User save(UserDto userDto) {
        return userRepository.save(new User(userDto, passwordEncryptionService.encrypt(userDto.getPassword())));
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> all() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User update(UserDto userDto, String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new UserNotFoundException(userId);
        User user = optionalUser.get();
        user.update(userDto);
        return userRepository.save(user);
    }
}
