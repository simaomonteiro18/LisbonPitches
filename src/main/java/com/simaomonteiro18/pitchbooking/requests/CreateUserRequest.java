package com.simaomonteiro18.pitchbooking.requests;

public record CreateUserRequest (String name, String password, String email, String phone, String city) {

}
