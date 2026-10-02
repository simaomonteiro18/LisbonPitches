package com.simaomonteiro18.lisbonpitches.services;

import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.exceptions.WeakPasswordException;
import com.simaomonteiro18.lisbonpitches.repositories.UserRepository;
import com.simaomonteiro18.lisbonpitches.requests.CreateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    CreateUserRequest request = new CreateUserRequest("Simão", "simaom", "Abcdef1!", "sasm07@gmail.com", "912345678", "Sintra");

    @Test
    @DisplayName("Teste a Registo com Password Válida")
    public void createWithValidPassword() throws Exception {

        User user = userService.create(request);

        assertNotNull(user);

        verify(userRepository).save(any());

    }

    CreateUserRequest requestWeak = new CreateUserRequest("Daniel", "dani", "abc", "dani@gmail.com", "987654321", "Cacém");

    @Test
    @DisplayName("Teste a Registo com Password Fraca")
    public void createWithWeakPassword() throws Exception {

        WeakPasswordException e = assertThrows(WeakPasswordException.class, () -> userService.create(requestWeak));

        assertTrue(e.getMessage().contains("pelo menos 8 caracteres"));
        assertTrue(e.getMessage().contains("uma letra maiúscula"));
        assertTrue(e.getMessage().contains("um símbolo"));
        assertTrue(e.getMessage().contains("um número"));

        verify(userRepository, never()).save(any());

    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "abcdefgh", "ABCDEFGH", "Abcdefg1", "Abcdefg!", "Abc1!"})
    @DisplayName("Teste a Registo com várias passwords fracas")
    public void createWithWeakPasswords(String password) throws Exception {

        CreateUserRequest requestTemplate = new CreateUserRequest("Daniel", "dani", password, "dani@gmail.com", "987654321", "Cacém");

        assertThrows(WeakPasswordException.class, () -> userService.create(requestTemplate));

        verify(userRepository, never()).save(any());

    }

}
