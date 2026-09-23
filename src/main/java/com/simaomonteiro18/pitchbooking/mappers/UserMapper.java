package com.simaomonteiro18.pitchbooking.mappers;

import com.simaomonteiro18.pitchbooking.dtos.UserSummaryDTO;
import com.simaomonteiro18.pitchbooking.entities.User;

public class UserMapper {

    public static UserSummaryDTO toDTO(User user) {

        UserSummaryDTO userSummaryDTO = new UserSummaryDTO(user.getId(), user.getName(), user.getUsername(), user.getEmail(), user.getPhone(), user.getCity());

        return userSummaryDTO;

    }

}
