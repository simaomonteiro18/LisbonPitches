package com.simaomonteiro18.pitchbooking.controllers;

import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.exceptions.ResourceNotFoundException;
import com.simaomonteiro18.pitchbooking.services.JwtService;
import com.simaomonteiro18.pitchbooking.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    User user = new User("Simão", "12345", "sm@gmail.com", "912345678", "Sintra");

    @Test
    @DisplayName("Teste a sucesso de pesquisa de User via UserController")
    public void testSuccessOfUserControllerFindingUser() throws Exception {

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1")).andExpect(status().isOk());

    }

    @Test
    @DisplayName("Teste a insucesso de pesquisa de User via UserController")
    public void testFailOfUserControllerFindingUser() throws Exception {

        when(userService.findById(999L)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/users/999")).andExpect(status().isNotFound());

    }

}
