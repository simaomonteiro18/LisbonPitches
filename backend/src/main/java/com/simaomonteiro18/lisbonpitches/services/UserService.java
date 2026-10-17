package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.exceptions.*;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import com.simaomonteiro18.lisbonpitches.requests.CreateUserRequest;
import com.simaomonteiro18.lisbonpitches.requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));

        return user;

    }

    public User create(CreateUserRequest request) {

        validatePassword(request.password());

        if (userRepository.findByEmail(request.email()) != null) {
            throw new EmailConflictException("Email já existe.");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameConflictException("Username já existe.");
        }

        User user = new User(request.name(), request.username(), passwordEncoder.encode(request.password()), request.email(), request.phone(), request.city());

        userRepository.save(user);

        return user;

    }

    public User login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email());

        if (user == null) {
            throw new InvalidCredentialsException("Credenciais Inválidas.");
        }

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            return user;
        } else {
            throw new InvalidCredentialsException("Credenciais Inválidas.");
        }

    }

    private void validatePassword(String password) {

        List<String> missing = new ArrayList<>();

        boolean lowerLetters = false;
        boolean upperLetters = false;
        boolean symbol = false;
        boolean digit = false;

        if (password.length() < 8) {
            missing.add("pelo menos 8 caracteres");
        }

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                lowerLetters = true;
            }
            if (Character.isUpperCase(c)) {
                upperLetters = true;
            }
            if (!Character.isLetterOrDigit(c)) {
                symbol = true;
            }
            if (Character.isDigit(c)) {
                digit = true;
            }
        }

        if (lowerLetters == false) {
            missing.add("uma letra minúscula");
        }
        if (upperLetters == false) {
            missing.add("uma letra maiúscula");
        }
        if (symbol == false) {
            missing.add("um símbolo");
        }
        if (digit == false) {
            missing.add("um número");
        }

        if (!missing.isEmpty()) {
            throw new WeakPasswordException("A password precisa de: " + String.join(", ", missing));
        }

    }

}
