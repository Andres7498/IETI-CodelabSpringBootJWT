package org.adaschool.api.controller.auth;

import org.adaschool.api.repository.user.User;
import org.adaschool.api.security.encrypt.PasswordEncryptionService;
import org.adaschool.api.service.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

import static org.adaschool.api.utils.Constants.TOKEN_DURATION_MINUTES;

@RestController
@RequestMapping("v1/auth")
public class AuthController {

    private final UsersService usersService;

    private final PasswordEncryptionService passwordEncryptionService;

    @Value("${jwt.secret}")
    String secret;

    public AuthController(@Autowired UsersService usersService, @Autowired PasswordEncryptionService passwordEncryptionService) {
        this.usersService = usersService;
        this.passwordEncryptionService = passwordEncryptionService;
    }

    @PostMapping
    public TokenDto login(@RequestBody LoginDto loginDto) {
        //TODO Implement this method
        return null;
    }


    private String generateToken(User user, Date expirationDate) {
        //TODO Implement this method
        return null;
    }

    private TokenDto generateTokenDto(User user) {
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.MINUTE, TOKEN_DURATION_MINUTES);
        String token = generateToken(user, expirationDate.getTime());
        return new TokenDto(token, expirationDate.getTime());
    }
}
