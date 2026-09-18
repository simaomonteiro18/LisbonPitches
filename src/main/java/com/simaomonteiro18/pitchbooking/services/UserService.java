package com.simaomonteiro18.pitchbooking.services;

import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.exceptions.InvalidCredentialsException;
import com.simaomonteiro18.pitchbooking.exceptions.ResourceNotFoundException;
import com.simaomonteiro18.pitchbooking.repositories.UserRepository;
import com.simaomonteiro18.pitchbooking.requests.CreateUserRequest;
import com.simaomonteiro18.pitchbooking.requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        User user = new User(request.name(), passwordEncoder.encode(request.password()), request.email(), request.phone(), request.city());

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

}
