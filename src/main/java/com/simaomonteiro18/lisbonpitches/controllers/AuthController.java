package com.simaomonteiro18.lisbonpitches.controllers;

import com.simaomonteiro18.lisbonpitches.dtos.LoginDTO;
import com.simaomonteiro18.lisbonpitches.dtos.UserSummaryDTO;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.mappers.UserMapper;
import com.simaomonteiro18.lisbonpitches.requests.LoginRequest;
import com.simaomonteiro18.lisbonpitches.services.JwtService;
import com.simaomonteiro18.lisbonpitches.services.UserService;
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
