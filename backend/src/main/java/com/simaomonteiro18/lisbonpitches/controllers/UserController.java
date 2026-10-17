package com.simaomonteiro18.lisbonpitches.controllers;

import com.simaomonteiro18.lisbonpitches.dtos.UserSummaryDTO;
import com.simaomonteiro18.lisbonpitches.entities.User;
import com.simaomonteiro18.lisbonpitches.mappers.UserMapper;
import com.simaomonteiro18.lisbonpitches.requests.CreateUserRequest;
import com.simaomonteiro18.lisbonpitches.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserSummaryDTO> findById(@PathVariable Long id) {

        User user = userService.findById(id);
        
        UserSummaryDTO userSummaryDTO = UserMapper.toDTO(user);

        return ResponseEntity.ok().body(userSummaryDTO);

    }

    @PostMapping
    public ResponseEntity<UserSummaryDTO> createUser(@RequestBody CreateUserRequest request) {

        User user = userService.create(request);

        UserSummaryDTO userSummaryDTO = UserMapper.toDTO(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userSummaryDTO);

    }

}
