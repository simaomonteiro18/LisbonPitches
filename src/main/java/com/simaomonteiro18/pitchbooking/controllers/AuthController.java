package com.simaomonteiro18.pitchbooking.controllers;

import com.simaomonteiro18.pitchbooking.dtos.LoginDTO;
import com.simaomonteiro18.pitchbooking.dtos.UserSummaryDTO;
import com.simaomonteiro18.pitchbooking.entities.User;
import com.simaomonteiro18.pitchbooking.mappers.UserMapper;
import com.simaomonteiro18.pitchbooking.requests.LoginRequest;
import com.simaomonteiro18.pitchbooking.services.JwtService;
import com.simaomonteiro18.pitchbooking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@RequestBody LoginRequest request) {

        User user = userService.login(request);

        String token = jwtService.tokenGenerator(user);

        UserSummaryDTO userSummaryDTO = UserMapper.toDTO(user);

        LoginDTO loginDTO = new LoginDTO(token, userSummaryDTO);

        return ResponseEntity.ok().body(loginDTO);

    }

}
