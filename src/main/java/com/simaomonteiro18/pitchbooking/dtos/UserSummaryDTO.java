package com.simaomonteiro18.pitchbooking.dtos;

public class UserSummaryDTO {

    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String city;

    public UserSummaryDTO() {
    }

    public UserSummaryDTO(Long id, String name, String username, String email, String phone, String city) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

}
