package org.adaschool.api.controller;

import org.adaschool.api.controller.auth.AuthController;
import org.adaschool.api.controller.auth.LoginDto;
import org.adaschool.api.exception.InvalidCredentialsException;
import org.adaschool.api.repository.user.User;
import org.adaschool.api.repository.user.UserDto;
import org.adaschool.api.security.encrypt.PasswordEncryptionService;
import org.adaschool.api.service.user.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@TestPropertySource(properties = {"spring.data.mongodb.uri=mongodb://localhost/testdb", "jwt.secret=secret"})
public class AuthControllerTest {

    final String BASE_URL = "/v1/auth";

    @MockBean
    private UsersService usersService;

    @MockBean
    private PasswordEncryptionService passwordEncryptionService;
    @Autowired
    private AuthController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(controller).build();
    }


    @Test
    public void loginWithValidCredentialsGeneratesToken() throws Exception {
        String email = "ada@mail.com";
        String password = "password";
        LoginDto loginDto = new LoginDto(email, password);
        UserDto userDto = new UserDto("Ada", "Lovelace", email, password);
        User user = new User(userDto, "passwordHash");
        when(usersService.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncryptionService.isPasswordMatch(password, user.getEncryptedPassword())).thenReturn(true);
        String json = "{\"email\":\"ada@mail.com\",\"password\":\"password\"}";

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(notNullValue())))
                .andExpect(jsonPath("$.expirationDate", is(notNullValue())));

    }

    @Test
    public void loginWithNotExistingUserThrowsInvalidCredentialsException() throws Exception {
        try {
            String email = "ada@mail.com";
            when(usersService.findByEmail(email)).thenReturn(Optional.empty());
            String json = "{\"email\":\"ada@mail.com\",\"password\":\"password\"}";
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidCredentialsException));

            verify(usersService, times(1)).findByEmail(email);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof InvalidCredentialsException);
        }
    }

    @Test
    public void loginWithInvalidEmailPasswordThrowsInvalidCredentialsException() throws Exception {
        try {

            String email = "ada@mail.com";
            String password = "password";
            LoginDto loginDto = new LoginDto(email, password);
            UserDto userDto = new UserDto("Ada", "Lovelace", email, password);
            User user = new User(userDto, "passwordHash");
            when(usersService.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncryptionService.isPasswordMatch(password, user.getEncryptedPassword())).thenReturn(false);
            String json = "{\"email\":\"ada@mail.com\",\"password\":\"password\"}";

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isUnauthorized())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidCredentialsException));

            verify(usersService, times(1)).findByEmail(email);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof InvalidCredentialsException);
        }
    }
}
