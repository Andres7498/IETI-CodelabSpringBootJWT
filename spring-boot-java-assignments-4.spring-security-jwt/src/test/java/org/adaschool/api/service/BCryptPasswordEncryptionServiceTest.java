package org.adaschool.api.service;

import org.adaschool.api.security.encrypt.BCryptPasswordEncryptionService;
import org.adaschool.api.security.encrypt.PasswordEncryptionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.data.mongodb.uri=mongodb://localhost/testdb", "jwt.secret=secret"})
public class BCryptPasswordEncryptionServiceTest {

    private final PasswordEncryptionService passwordEncryptionService = new BCryptPasswordEncryptionService();

    @Test
    public void isPasswordMatchValidPasswordTest() {
        String password = "password";
        String encryptedPassword = passwordEncryptionService.encrypt(password);
        assertTrue(passwordEncryptionService.isPasswordMatch(password, encryptedPassword));
    }

    @Test
    public void isPasswordMatchInvalidPasswordTest() {
        String password = "password";
        String encryptedPassword = passwordEncryptionService.encrypt(password);
        assertFalse(passwordEncryptionService.isPasswordMatch("passwor", encryptedPassword));
    }

    @Test
    public void encryptPasswordTest() {
        String password = "password";
        String encryptedPassword = passwordEncryptionService.encrypt(password);
        assertNotEquals(password, encryptedPassword);

    }
}
