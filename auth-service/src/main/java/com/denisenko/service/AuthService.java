package com.denisenko.service;

import com.denisenko.entity.User;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@ApplicationScoped
public class AuthService {
    private static final Logger LOG = Logger.getLogger(AuthService.class);

    @Inject
    JWTParser jwtParser;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Transactional
    public Optional<Integer> registerUser(User user) {
        if (User.existsByUsername(user.getUsername())) return Optional.empty();
        user.setPassword(hashPassword(user.getPassword()));
        user.persist();
        LOG.info("User with username=" + user.getUsername() + " was registered successfully");
        return Optional.of(user.getId());
    }

    public Optional<String> login(User user) {
        LOG.info("Trying to find user with username=" + user.getUsername());
        Optional<User> userOptional = User.findByUsername(user.getUsername());

        if (userOptional.isEmpty()) {
            LOG.warn("User not found");
            return Optional.empty();
        }
        if (!checkPassword(user.getPassword(), userOptional.get().getPassword())) {
            LOG.warn("Invalid credentials. Password is incorrect");
            return Optional.empty();
        }

        return Optional.of(generateToken(userOptional.get().getId().toString()));
    }

    public boolean validateToken(String token) {
        try {
            if (token == null) return false;
            JsonWebToken jwt = jwtParser.parse(token);
            String userId = jwt.getSubject();
            return userId != null;
        } catch (ParseException e) {
            LOG.error("Token parsing failed", e);
            return false;
        }
    }

    public String extractUserId(String token) {
        try {
            JsonWebToken jwt = jwtParser.parse(token);
            return jwt.getSubject();
        } catch (ParseException e) {
            return null;
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    private String generateToken(String userId) {
        return Jwt.claims()
                .issuer(issuer)
                .subject(userId)
                .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                .sign();
    }
}
