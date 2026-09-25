package com.simaomonteiro18.lisbonpitches.dtos;

public class LoginDTO {

    private String token;
    private UserSummaryDTO userSummaryDTO;

    public LoginDTO() {
    }

    public LoginDTO(String token, UserSummaryDTO userSummaryDTO) {
        this.token = token;
        this.userSummaryDTO = userSummaryDTO;
    }

    public String getToken() {
        return token;
    }

    public UserSummaryDTO getUserSummaryDTO() {
        return userSummaryDTO;
    }
    
}
