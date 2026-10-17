package com.simaomonteiro18.lisbonpitches.requests;

public record CreateUserRequest (String name, String username, String password, String email, String phone, String city) {

}
