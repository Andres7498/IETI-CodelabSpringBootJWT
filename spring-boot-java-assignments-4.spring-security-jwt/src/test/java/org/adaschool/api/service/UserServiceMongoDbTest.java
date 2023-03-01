package org.adaschool.api.service;

import org.adaschool.api.repository.user.User;
import org.adaschool.api.repository.user.UserDto;
import org.adaschool.api.repository.user.UserRepository;
import org.adaschool.api.security.encrypt.PasswordEncryptionService;
import org.adaschool.api.service.user.UsersServiceMongoDb;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource(properties = {"spring.data.mongodb.uri=mongodb://localhost/testdb", "jwt.secret=secret"})
public class UserServiceMongoDbTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncryptionService encryptionService;

    @InjectMocks
    private UsersServiceMongoDb usersServiceMongoDb;


    @Test
    @Order(1)
    public void testFindAllUsers() {
        List<User> usersListMock = Arrays.asList(
                new User("1", "Ada", "Lovelace", "ada@mail.com", "123456789"),
                new User("2", "Ada", "Lovelace 2", "ada2@mail.com", "123456789"),
                new User("3", "Ada", "Lovelace 3", "ada3@mail.com", "123456789"),
                new User("4", "Ada", "Lovelace 4", "ada4@mail.com", "123456789")
        );
        Mockito.when(userRepository.findAll()).thenReturn(usersListMock);
        List<User> users = usersServiceMongoDb.all();
        Assertions.assertNotNull(users);
        Assertions.assertTrue(users.size() > 0);
        Assertions.assertEquals("Lovelace 3", users.get(2).getLastName());
    }

    @Test
    @Order(2)
    public void testFindUserById() {
        Optional<User> userMock = Optional.of(new User("1", "Ada", "Lovelace", "ada@mail.com", "123456789"));
        Mockito.when(userRepository.findById("1")).thenReturn(userMock);
        Optional<User> user = usersServiceMongoDb.findById("1");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("Ada", user.get().getName());
    }

    @Test
    @Order(3)
    public void testCreateUser() {
        UserDto userFromController = new UserDto("Ada", "Lovelace", "ada@mail.com", "123456789");
        String encryptedPassword = "asdqw--*)77777999";
        User user = new User(userFromController, encryptedPassword);
        Mockito.when(userRepository.save(any())).thenReturn(user);
        User userSaved = usersServiceMongoDb.save(userFromController);
        Assertions.assertNotNull(userSaved);
        Assertions.assertEquals("ada@mail.com", userSaved.getEmail());
    }

    @Test
    @Order(4)
    public void testDeleteUserById() {
        Optional<User> userToDelete = Optional.of(new User("63dc745f9c7ac326f2fd54f0", "Ada", "Lovelace", "ada@mail.com", "123456789"));
        Mockito.when(userRepository.findById("63dc745f9c7ac326f2fd54f0")).thenReturn(userToDelete);
        usersServiceMongoDb.deleteById("63dc745f9c7ac326f2fd54f0");
        Mockito.verify(userRepository, Mockito.times(1)).deleteById("63dc745f9c7ac326f2fd54f0");
    }

}
