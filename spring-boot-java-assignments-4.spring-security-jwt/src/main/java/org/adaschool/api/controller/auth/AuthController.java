package org.adaschool.api.controller.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.adaschool.api.exception.InvalidCredentialsException;
import org.adaschool.api.repository.user.User;
import org.adaschool.api.security.encrypt.PasswordEncryptionService;
import org.adaschool.api.service.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.adaschool.api.utils.Constants.CLAIMS_ROLES_KEY;
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
        Optional<User> user = usersService.findByEmail(loginDto.getEmail());
        if (user.isPresent() && passwordEncryptionService.isPasswordMatch(loginDto.getPassword(), user.get().getEncryptedPassword())) {
            return generateTokenDto(user.get());
        }
        throw new InvalidCredentialsException();
    }

    private String generateToken(User user, Date expirationDate) {
        return Jwts.builder()
                .setSubject(user.getId())
                .claim(CLAIMS_ROLES_KEY, user.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private TokenDto generateTokenDto(User user) {
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.MINUTE, TOKEN_DURATION_MINUTES);
        String token = generateToken(user, expirationDate.getTime());
        return new TokenDto(token, expirationDate.getTime());
    }
}